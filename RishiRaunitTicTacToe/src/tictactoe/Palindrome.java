package tictactoe;

public class Palindrome
{
	 
		private String str;
		private boolean palindrome;
	 
		public Palindrome (String s )
		{
			str = s;
			palindrome = isPalindrome () ;
		}
	 
		/**Returns the following for racecar and like 
		 * String: racecar 
		 * Palindrome: true
		 * 
		 * String: like
		 * Palindrome: false
		 * 
		 * */
		public String toString() 
		{
			/* to be implemented in part (a) */
	 
			return "String:" + str + "\n" + "Palindrome: " + isPalindrome();
			String reversed = "";
			for(int i = str.length()-1; i>= 0; i--)
			{
				reversed += ""+str.charAt(i)).toLowerCase();
				
			}
			return reversed.equals(str);
			
		}
	 
	 
		/**Returns true if str is a Palindrome; false otherwise 
		 * Use all lowercase letters */
		private boolean isPalindrome () 
		{
			/* to be implemented in part (b) */
	 
}
	








public static void main(String[] args)
	{
		

	}

}
