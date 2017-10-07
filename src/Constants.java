
public class Constants {

	public static Object get(String key) {
		return null;
	}

	public static void add(String key, Object val) {

	}

	public static void addAll(Constant[] list) {
		for (int i = 0; i < list.length; i++) {
			System.out.println(list[i].toString());
		}
	}

	protected class Constant {
		public String key;
		public String val;
	}

}
