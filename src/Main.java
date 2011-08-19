package nz.gen.geek_central.Compass3D;
/*
    Display a 3D compass arrow using OpenGL.

    Written by Lawrence D'Oliveiro <ldo@geek-central.gen.nz>.
*/

public class Main extends android.app.Activity
  {
    android.widget.TextView MessageView;
    VectorView Graphical;
    android.hardware.SensorManager SensorMan;
    android.hardware.Sensor Compass = null;
    android.hardware.SensorEventListener Listen = null;

    protected void ClearListener()
      {
        if (Compass != null && Listen != null)
          {
            SensorMan.unregisterListener(Listen, Compass);
          } /*if*/
      } /*ClearListener*/

    protected void InstallListener()
      {
        if (Compass != null && Listen != null)
          {
            SensorMan.registerListener
              (
                Listen,
                Compass,
                android.hardware.SensorManager.SENSOR_DELAY_UI
              );
          } /*if*/
      } /*InstallListener*/

    @Override
    public void onCreate
      (
        android.os.Bundle savedInstanceState
      )
      {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        MessageView = (android.widget.TextView)findViewById(R.id.message);
        Graphical = (VectorView)findViewById(R.id.vector_view);
        SensorMan = (android.hardware.SensorManager)getSystemService(SENSOR_SERVICE);
        Compass = SensorMan.getDefaultSensor(android.hardware.Sensor.TYPE_ORIENTATION);
        if (Compass != null)
          {
            Listen = new android.hardware.SensorEventListener()
                  {

                    public void onAccuracyChanged
                      (
                        android.hardware.Sensor TheSensor,
                        int NewAccuracy
                      )
                      {
                      /* ignore for now */
                      } /*onAccuracyChanged*/

                    public void onSensorChanged
                      (
                        android.hardware.SensorEvent Event
                      )
                      {
                        final java.io.ByteArrayOutputStream MessageBuf = new java.io.ByteArrayOutputStream();
                        final java.io.PrintStream Msg = new java.io.PrintStream(MessageBuf);
                        Msg.printf
                          (
                            "Sensor event at %.9f accuracy %d\nValues(%d): (",
                            Event.timestamp / Math.pow(10.0d, 9),
                            Event.accuracy,
                            Event.values.length
                          );
                        for (int i = 0; i < Event.values.length; ++i)
                          {
                            if (i != 0)
                              {
                                Msg.print(", ");
                              } /*if*/
                            Msg.printf("%.6f°", Event.values[i]);
                          } /*for*/
                        Msg.print(")\n");
                        Msg.flush();
                        MessageView.setText(MessageBuf.toString());
                        Graphical.SetData(Event.values);
                      } /*onSensorChanged*/

                  } /*SensorEventListener*/;
          }
        else
          {
            MessageView.setText("No compass hardware present");
          } /*if*/
      } /*onCreate*/

    @Override
    public void onPause()
      {
        super.onPause();
        ClearListener(); /* conserve battery */
      } /*onPause*/

    @Override
    public void onResume()
      {
        super.onResume();
        InstallListener();
      } /*onResume*/

  } /*Main*/
