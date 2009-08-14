
package Sirius.util.collections;

// renames

public class StringMapsString extends java.util.Hashtable
{


//-----------------------------------------------------------

	public void add(String descriptor,String aString) throws Exception
	{
		super.put(descriptor,aString);

		if(!containsKey(descriptor))
		throw new Exception("Einf\u00FCgen des Schl\u00FCssels nicht geklappt :"+descriptor);
	}// end add

//---------------------------------------------------------

	public StringMapsString()
	{super();}


	public StringMapsString(int initialCapacity)
	{super(initialCapacity);}

//---------------------------------------------------------

	public StringMapsString(int initialCapacity, float loadFactor)
	{super(initialCapacity,loadFactor);}

//---------------------------------------------------------

	public String getStringValue(String descriptor) throws Exception
	{

		if(containsKey(descriptor))
			{
			java.lang.Object candidate = super.get(descriptor);

			   if (candidate instanceof String)
			   return ((String) candidate);

			throw new java.lang.NullPointerException("Entry is not a String :" + descriptor);
			}// endif

	throw new java.lang.NullPointerException("No entry :"+ descriptor);
	}

	/////// containsIntKey/////////////////////////////////
	public boolean containsStringKey(String key)
	{return super.containsKey(key);}


}
