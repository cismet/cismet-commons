
package Sirius.util.collections;


public class IntMapsString extends java.util.Hashtable
{


		IntMapsString()
		{super();}


		IntMapsString(int initialCapacity, float loadFactor)
		{super(initialCapacity,loadFactor);}

		public void add(int id,String aString)
		{
			super.put(new Integer(id),aString);
		}// end add


		public String getStringValue(int id) throws Exception
		{
		Integer key = new Integer(id);

			if(super.containsKey(key))
			{
			java.lang.Object candidate = super.get(key);

			   if (candidate instanceof java.lang.String)
			   return ((String) candidate);

			throw new java.lang.NullPointerException("Entry is not a String :" + id);
			}// endif

		throw new java.lang.NullPointerException("No entry :"+ id); // to be changed in further versions when exception concept is accomplished
		}

		/////// containsIntKey/////////////////////////////////
		public boolean containsIntKey(int key)
		{return super.containsKey(new Integer(key));}










}
