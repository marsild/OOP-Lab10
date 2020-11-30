package it.unibo.oop.lab.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public final class AnotherConcurrentGUI extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = -8630968055862320453L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");
    private final JLabel display2 = new JLabel();
    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        this.display.setText("0");
        this.display2.setText("Time: 0s");
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        panel.add(display2);
        this.getContentPane().add(panel);
        this.setVisible(true);
        final Agent agent = new Agent();
        new Thread(agent).start();
        stop.addActionListener(e -> agent.stopCounting());
        up.addActionListener(e -> agent.setUpFlag());
        down.addActionListener(e -> agent.setDownFlag());
    }
    private class Agent implements Runnable {
        private volatile boolean stop;
        private volatile boolean direction = true; //true incrementa, false decrementa
        private volatile int counter;
        private volatile int counter2;

        @Override
        public void run() {
            while (!this.stop && this.counter2 <= 100) {
                try {
                    /*
                     * All the operations on the GUI must be performed by the
                     * Event-Dispatch Thread (EDT)!
                     */
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(Integer.toString(this.counter)));
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display2.setText("Time: " + Double.toString((double) this.counter2 / 10) + "s"));
                    if (direction) {
                        this.counter++;
                        this.counter2++;
                    } else {
                        this.counter--;
                        this.counter2++;
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    /*
                     * This is just a stack trace print, in a real program there
                     * should be some logging and decent error reporting
                     */
                    ex.printStackTrace();
                }
            }
            AnotherConcurrentGUI.this.up.setEnabled(false);
            AnotherConcurrentGUI.this.down.setEnabled(false);
            AnotherConcurrentGUI.this.stop.setEnabled(false);
        }

        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
        }
        /**
         * External command to increment.
         */
        public void setUpFlag() {
            this.direction = true;
        }
        /**
         * External command to decrement.
         */
        public void setDownFlag() {
            this.direction = false;
        }
    }
}

