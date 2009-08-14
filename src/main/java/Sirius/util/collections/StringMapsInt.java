
package Sirius.util.collections;


public class StringMapsInt extends java.util.Hashtable
{

	public StringMapsInt(int initialCapacity,float loadFactor)
	{super(initialCapacity,loadFactor);}

	public StringMapsInt()
	{super();}

	public void add(String descriptor,int sqlID)
	{
		super.put(descriptor,new Integer(sqlID));
	}// end add


	///////////////////////////////////////////

	public int getIntValue(String descriptor) throws Exception
	{

	if(super.containsKey(descriptor))
	{
	java.lang.Object candidate = super.get(descriptor);

	   if (candidate instanceof Integer)
	   return ((Integer) candidate).intValue();

	throw new java.lang.NullPointerException("Entry is not a Integer :" + descriptor);
	}// endif

	throw new java.lang.NullPointerException("No entry :"+ descriptor); // to be changed in further versions when exception concept is accomplished
	}

	/////// containsIntKey/////////////////////////////////
	public boolean containsStringKey(String key)
	{return super.containsKey(key);}










}
