import com.cruzsbrian.robolog.Constants;
import com.cruzsbrian.robolog.Log;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        Constants.loadFromFile();
        Log.startServer(8765);


		Thread.sleep(10000);

		System.out.println("Sending test data");
		double voltage = 12.0;
		//Log.log("voltage", Double.toString(voltage));
		for (int i = 0; i < 500; i++) {
			//Log.log("elevator:motorvoltage", Double.toString(voltage));
			//Log.log("elevator:position", Double.toString(voltage / 3));

			Log.add("voltage", voltage);
			Log.add("position", voltage / 3);

			voltage -= 0.01 * voltage;

			Thread.sleep(5);
		}
    }

}
