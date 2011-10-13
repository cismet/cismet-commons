CREATE OR REPLACE FUNCTION calculate_points_gewaesserumfeld_func(kartierabsch_id integer)
  RETURNS integer AS
'
DECLARE
    -- necessary meta information
    gew_typ_id         INTEGER;
    gew_rand_links_id  INTEGER;
    gew_rand_rechts_id INTEGER;
    fla_nutz_links_id  INTEGER;
    fla_nutz_rechts_id INTEGER;
    num_entries        INTEGER;

    -- ratings
    rating_total        NUMERIC := 0;
    rating_rand_links   INTEGER;
    rating_rand_rechts  INTEGER;
    rating_fla_links    INTEGER;
    rating_fla_rechts   INTEGER;
    num_rating        INTEGER := 0;
    malus_umfeldstruktur_links   NUMERIC;
    malus_umfeldstruktur_rechts   NUMERIC;
BEGIN

    SELECT COUNT(*) INTO num_entries
                    FROM gewaesserumfeld
                    WHERE kartierabschnitt_id  = kartierabsch_id;
    IF (num_entries = 0 )THEN
        RAISE DEBUG ''Kein Eintrag fuer gewaesserumfeld vorhanden. --> Abbruch'';
        RETURN kartierabsch_id;
    END IF;
    
    RAISE DEBUG ''------- AUSWERTUNG GEWAESSERUMFELD -------'';
    RAISE DEBUG ''Kartierabschnitt ID %'', kartierabsch_id;

    --
    -- collect necessary meta information
    --
    SELECT gewaessertyp_id INTO gew_typ_id FROM kartierabschnitt
           WHERE id = kartierabsch_id;
    RAISE DEBUG ''Gewassertyp ID = %'', gew_typ_id;

    SELECT flaechennutzung_links_id INTO fla_nutz_links_id FROM gewaesserumfeld
           WHERE kartierabschnitt_id = kartierabsch_id;
    RAISE DEBUG ''Flaechennutzung links id = %'', fla_nutz_links_id;

    SELECT flaechennutzung_rechts_id INTO fla_nutz_rechts_id FROM gewaesserumfeld
           WHERE kartierabschnitt_id = kartierabsch_id;
    RAISE DEBUG ''Flaechennutzung rechts id = %'', fla_nutz_rechts_id;

    SELECT gewaesserrandstreifen_links_id INTO gew_rand_links_id
           FROM gewaesserumfeld
           WHERE kartierabschnitt_id = kartierabsch_id;
    RAISE DEBUG ''Gewaesserrandstreifen links id = %'', gew_rand_links_id;

    SELECT gewaesserrandstreifen_rechts_id INTO gew_rand_rechts_id
           FROM  gewaesserumfeld
           WHERE kartierabschnitt_id = kartierabsch_id;
    RAISE DEBUG ''Gewaesserrandstreifen rechts id = %'', gew_rand_rechts_id;


    --
    -- collect ratings
    --
    SELECT bewertung INTO rating_rand_links
           FROM gewaesserrandstreifen_auswertung
           WHERE id_gewaessertyp = gew_typ_id
             AND id_gewaesserrandstreifen = gew_rand_links_id;
    RAISE DEBUG ''-> Gewaesserrandstreifen links: %'', rating_rand_links;

    SELECT bewertung INTO rating_rand_rechts
           FROM gewaesserrandstreifen_auswertung
           WHERE id_gewaessertyp = gew_typ_id
             AND id_gewaesserrandstreifen = gew_rand_rechts_id;
    RAISE DEBUG ''-> Gewaesserrandstreifen rechts: %'', rating_rand_rechts;

    SELECT bewertung INTO rating_fla_links
           FROM flaechennutzung_auswertung
           WHERE id_gewaessertyp = gew_typ_id
             AND id_flaechennutzung = fla_nutz_links_id;
    RAISE DEBUG ''-> Flaechennutzung links: %'', rating_fla_links;

    SELECT bewertung INTO rating_fla_rechts
           FROM flaechennutzung_auswertung
           WHERE id_gewaessertyp = gew_typ_id
             AND id_flaechennutzung = fla_nutz_rechts_id;
    RAISE DEBUG ''-> Flaechennutzung rechts: %'', rating_fla_rechts;
    
    SELECT SUM(bewertung) INTO malus_umfeldstruktur_links
           FROM schaedlicheumfeldstrukturen_auswertung, 
                gewaesserumfeld_hat_schaedlicheumfeldstrukturen
           WHERE kartierabschnitt_id = kartierabsch_id AND
                 schaedlicheumfeldstrukturen_id = id_schaedlicheumfeldstrukturen AND
                 id_gewaessertyp = gew_typ_id AND
                 anzahl_links > 0;
    RAISE DEBUG ''-> Malus schaedliche Umfeldstrukturen links: %''
                 , malus_umfeldstruktur_links;

    --
    -- pay attention to the given max malus values explained in the scheme
    --
    IF (gew_typ_id = 11 OR gew_typ_id = 12) AND 
       malus_umfeldstruktur_links < -1 THEN
        malus_umfeldstruktur_links := -1;
        RAISE DEBUG ''--> Malus schaedliche Umfeldstrukturen links korrigiert: %''
                     , malus_umfeldstruktur_links;
    ELSEIF malus_umfeldstruktur_links < -3 THEN
        malus_umfeldstruktur_links = -3;
        RAISE DEBUG ''--> Malus schädliche Umfeldstrukturen links korrigiert: %''
                     , malus_umfeldstruktur_links;
    END IF;
    
    SELECT SUM(bewertung) INTO malus_umfeldstruktur_rechts
           FROM schaedlicheumfeldstrukturen_auswertung, 
                gewaesserumfeld_hat_schaedlicheumfeldstrukturen
           WHERE kartierabschnitt_id = kartierabsch_id AND
                 schaedlicheumfeldstrukturen_id = id_schaedlicheumfeldstrukturen AND
                 id_gewaessertyp = gew_typ_id AND
                 anzahl_rechts > 0;
    RAISE DEBUG ''-> Malus schädliche Umfeldstrukturen rechts: %''
                 , malus_umfeldstruktur_rechts;
    --
    -- pay attention to the given max malus values explained in the scheme
    --
    IF (gew_typ_id = 11 OR gew_typ_id = 12) AND 
        malus_umfeldstruktur_rechts < -1 THEN
        malus_umfeldstruktur_rechts := -1;
        RAISE DEBUG ''--> Malus schaedliche Umfeldstrukturen rechts korrigiert: %''
                     , malus_umfeldstruktur_rechts;
    ELSEIF malus_umfeldstruktur_rechts < -3 THEN
        malus_umfeldstruktur_rechts := -3;
        RAISE DEBUG ''--> Malus schaedliche Umfeldstrukturen rechts korrigiert: %''
                     , malus_umfeldstruktur_rechts;
    END IF;
    --
    -- calculate the total rating and return it
    --
    IF rating_rand_links > 0 THEN
        rating_total := rating_total + rating_rand_links;
        num_rating := num_rating + 1;
    END IF;

    IF rating_rand_rechts > 0 THEN
        rating_total := rating_total + rating_rand_rechts;
        num_rating := num_rating + 1;
    END IF;

    IF rating_fla_links > 0 THEN
        rating_total := rating_total + rating_fla_links;
        num_rating := num_rating + 1;
    END IF;

    IF rating_fla_rechts > 0 THEN
        rating_total := rating_total + rating_fla_rechts;
        num_rating := num_rating + 1;
    END IF;
    
    IF malus_umfeldstruktur_links < 0 THEN
        rating_total := rating_total + malus_umfeldstruktur_links;
    END IF;
    
    IF malus_umfeldstruktur_rechts < 0 THEN
        rating_total := rating_total + malus_umfeldstruktur_rechts;
    END IF;

    UPDATE gewaesserumfeld SET summe_punktzahl = rating_total, 
                               anzahl_kriterien = num_rating
           WHERE kartierabschnitt_id = kartierabsch_id;
    RAISE DEBUG ''=> Gewaesserumfeld gesamt: %'', rating_total;
    RAISE DEBUG ''=> Anzahl der Kriterien: %'', num_rating;

    -- Call Gesamtbewertungs-function
    PERFORM update_points_kartierabschnitt_func(kartierabsch_id);
    RETURN kartierabsch_id;
END;
'
  LANGUAGE 'plpgsql' VOLATILE
  COST 100;