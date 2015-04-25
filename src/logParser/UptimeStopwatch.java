/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logParser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Timer;

/**
 *
 * @author Asmodean
 */
public class UptimeStopwatch implements ActionListener {
    
    private long startTime;
    private Timer timer;
    
    /**
     * Initializes a running stopwatch using the given input as a basis for the start time.
     * @param input in the form of "[CHAT WINDOW TEXT] [Sun Oct 02 20:10:49]"
     */
    public UptimeStopwatch(String input){
        setStartTime(input);
        timer = new Timer(1000, this);
        timer.start();
    }

    /**
     * This method runs RegEx on the input to filter out the necessary information.
     * @param input in the form of "[CHAT WINDOW TEXT] [Sun Oct 02 20:10:49]
     */
    private void setStartTime(String input) {
//        [CHAT WINDOW TEXT] [Sun Oct 02 20:10:49]
        Pattern LINE_PATTERN = Pattern.compile("\\[\\w*\\s\\w*\\s\\w*]\\s\\[(\\w*\\s\\w*\\s\\d*\\s\\d*:\\d*:\\d*)]");
        Matcher m = LINE_PATTERN.matcher(input);
        startTime = stringToDate((m.replaceAll("$1"))).getTime();
    }
    
    /**
     * Help method for setStartTime() to make a Date object out of string input of the form "Sun Oct 02 20:10:49"
     * where Sun and Oct is ignored, 02 is the number of day, and the rest follows HH:MM:SS
     * @param string of the form "Sun Oct 02 20:10:49"
     * @return Date object with set year, month, day, hour, minute and second.<br><br>
     * Year and month are special in the sense that they are not derived from the input, but are instead taken from the
     * current time. This shouldn't really be an issue and is done to simplify calculations.
     */
    private Date stringToDate(String string){
        StringBuilder sb = new StringBuilder(string);
        Date dateToday = new Date();
        int year = dateToday.getYear();
        int month = dateToday.getMonth();
        int day = Integer.parseInt(sb.substring(8, 10));
        int hour = Integer.parseInt(sb.substring(11, 13));
        int minute = Integer.parseInt(sb.substring(14, 16));
        int second = Integer.parseInt(sb.substring(17, 19));
        Date completeDateObject = new Date(year, month, day, hour, minute, second);
        return completeDateObject;
    }
    
    /**
     * Method that returns the current uptime in milliseconds formatted for printing.
     * @return String containing current uptime in milliseconds formatted like HH:MM:SS
     */
    private String getUptime() {
//        System.out.println(new Time(System.currentTimeMillis()).toString());
//        System.out.println(new Time(startTime).toString());
        return (new Time(System.currentTimeMillis() - startTime - 3600000).toString());
    }
    
    /**
     * Called by the thread at the given interval (1000). Updates the jTextFieldUptime component.
     * @param e - the event.
     */
    public void actionPerformed(ActionEvent e) {
        JFrameParser.jTextFieldUptime.setText("Uptime: " + getUptime());
    }

    /**
     * Method to reset the counter.
     * @param string in the form of "Sun Oct 02 20:10:49"
     */
    public void restart(String s) {
        setStartTime(s);
    }

    /**
     * This stops the counter and clears jTextFieldUptime.
     */
    public void stop(){
        timer.stop();
        JFrameParser.jTextFieldUptime.setText("Uptime: 00:00:00");
    }
}