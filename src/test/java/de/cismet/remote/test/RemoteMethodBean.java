package de.cismet.remote.test;

/**
 *
 * @author pd
 */
public class RemoteMethodBean {

    private String string = "String";

    /**
     * Get the value of string
     *
     * @return the value of string
     */
    public String getString() {
        return string;
    }

    /**
     * Set the value of string
     *
     * @param string new value of string
     */
    public void setString(String string) {
        this.string = string;
    }

    private double dbl = 31.337d;

    /**
     * Get the value of dbl
     *
     * @return the value of dbl
     */
    public double getDbl() {
        return dbl;
    }

    /**
     * Set the value of dbl
     *
     * @param dbl new value of dbl
     */
    public void setDbl(double dbl) {
        this.dbl = dbl;
    }

    private boolean boolValue = true;

    /**
     * Get the value of bool
     *
     * @return the value of bool
     */
    public boolean isBoolValue() {
        return boolValue;
    }

    /**
     * Set the value of bool
     *
     * @param bool new value of bool
     */
    public void setBoolValue(boolean boolValue) {
        this.boolValue = boolValue;
    }

}
