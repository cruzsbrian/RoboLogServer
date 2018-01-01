# RoboLog
A dashboard for FRC programming.

This is the server side of RoboLog, which runs on the RoboRIO. It is responsible for collecting and sending data to the client side, which runs on a programmer's computer.

## Installation
### Client (laptop side)
1. Download the latest RoboLog Client from the releases page.
2. Unzip the file and open `index.html` to open the dashboard.
3. (Recommended) Make a shortcut to `index.html` on your desktop for easy access.
4. Usage instructions for the client can be found in the [RoboLogClient](https://github.com/cruzsbrian/RoboLogClient) repository.

### Server (robot side)
1. Download the latest RoboLog Server from the releases page.
2. Place the .jar file in `{your home directory}/wpilib/user/java/lib`.
3. If Eclipse is already open, you need to relaunch it for the new library to be recognized.

## Usage
### Starting the Server
`Log.startServer(int port)`

Pass it the port number to open the server on. When connecting from the client, make sure to use the same number. Numbers above 1000 are usually open for use.

### Sending Graph Data
`Log.add(String key, Double value)` 

For example, `Log.add("flywheel-rpm", Robot.shooter.getFlywheelRpm())` could be used to generate a graph of a flywheel's speed. You must call `Log.add()` again every time you want to send another data point.

### Sending Logs
`Log.log(String subject, Object message)`

The subject should be some identifier for the log entry's topic, so that you can use it to filter logs on the client. The message can be any object that can be converted to a string (like ints, doubles, or strings themselves).

### Changing the Data Batch Timing
`Log.setDelay(int delay)`

By default, RoboLog sends data every 50ms. Calling this function overrides that value with a user-specified delay, in ms.

## Constants
### Loading Constants Initially
`Constants.loadFromFile()`

This will load the constants that are stored on the robot. If no file is found, it leaves the constants object empty.


### Getting Constants
`Constants.getAsInt(String key)`

This returns an integer representation of the constant for the given key. If it cannot be parsed as an integer, the function returns 0.

`Constants.getAsDouble(String key)`

This returns an double representation of the constant for the given key. If it cannot be parsed as a double, the function returns 0.

`Constants.getAsString(String key)`

This returns the raw value of the constant as a String.

## Example
Here's an example of how you might implement RoboLog in robot code.
```java
import com.cruzsbrian.robolog.Constants;
import com.cruzsbrian.robolog.Log;

import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {

	public void robotInit() {
		Constants.loadFromFile();
		Log.startServer(1234);
	}

	public void autonomousInit() {
		Log.log("general", "starting auto");
		autoRoutine.start();
	}

	public void autonomousPeriodic() {
		Log.add("distance-traveled", Robot.drivetrain.getDist());
		Log.add("yaw", Robot.drivetrain.getYaw());
	}
	
	public void teleopInit() {
		Log.log("general", "starting teleop");
	}

	public void teleopPeriodic() {
		// the following code would normally be elsewhere than Robot.java
		// but you get the idea
		
		if (shootButtonPressed()) {
			Log.log("shooter", "spinning up flywheel");
			shooter.spinUpTo(Constants.getAsInt("optimum-flywheel-speed"));
		}
		
		if (shooter.atSpeed()) {
			Log.log("shooter", "shooter at speed. starting feeder");
			feeder.feed();
		}
		
		if (shooter.spinning()) {
			Log.add("flywheel-speed", shooter.flywheelSpeed());
		}
	}
}
```

When the robot code starts, it loads the constants and starts the server so that the client can connect to it. In autonomous, it uses graphs to record the distance traveled and the robot's yaw at each step. In teleop, it uses logs to record that it attempted to spin up the flywheel and start the feeder. It also uses graphs to track the flywheel's speed, and a constant called "optimum-flywheel-speed," which can be updated from the client without having to re-deploy. 
