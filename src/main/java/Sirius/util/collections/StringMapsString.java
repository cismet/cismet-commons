
package Sirius.util.collections;

// renames

public class StringMapsString extends java.util.Hashtable
{


//-----------------------------------------------------------

	public void add(String descriptor,String aString) throws Exception
	{
		super.put(descriptor,aString);

		if(!containsKey(descriptor))
		throw new Exception("Could not insert key :"+descriptor);  // NOI18N
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

			throw new java.lang.NullPointerException("Entry is not a String :" + descriptor);  // NOI18N
			}// endif

	throw new java.lang.NullPointerException("No entry :"+ descriptor);  // NOI18N
	}

	/////// containsIntKey/////////////////////////////////
	public boolean containsStringKey(String key)
	{return super.containsKey(key);}


}
