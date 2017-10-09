
public class Test {

	public static void main(String[] args) throws InterruptedException {
        Constants.loadFromFile();
		Log.startServer(8765);

//		Thread.sleep(10000);
//
//		System.out.println("Sending test data");
//		double voltage = 12.0;
//		//Log.log("voltage", Double.toString(voltage));
//		for (int i = 0; i < 500; i++) {
//			Log.log("voltage", Double.toString(voltage));
//			Log.log("position", Double.toString(voltage / 3));
//
//			Log.add("voltage", voltage);
//			Log.add("position", voltage / 3);
//			Log.send();
//
//			voltage -= 0.01;
//
//			Thread.sleep(50);
//		}
	}

}
