package diningphilosophers;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Philosopher extends Thread {
    private final static int delai = 1000;
    private final ChopStick myLeftStick;
    private final ChopStick myRightStick;
    private boolean running = true;

    public Philosopher(String name, ChopStick left, ChopStick right) {
        super(name);
        myLeftStick = left;
        myRightStick = right;
    }

    private void think() throws InterruptedException {
        System.out.println("M."+this.getName()+" pense... ");
        sleep(delai+new Random().nextInt(delai+1));
        System.out.println("M."+this.getName()+" arrête de penser");
    }

    private void eat() throws InterruptedException {
        System.out.println("M."+this.getName() + " mange...");
        sleep(delai+new Random().nextInt(delai+1));
        //System.out.println("M."+this.getName()+" arrête de manger");
    }

    @Override
    public void run() {
        /* while (running) {
            try {
                think();
                // Aléatoirement prendre la baguette de gauche puis de droite ou l'inverse
                switch(new Random().nextInt(2)) {
                    case 0:
                        myLeftStick.take();
                        think(); // pour augmenter la probabilité d'interblocage
                        myRightStick.take();
                        break;
                    case 1:
                        myRightStick.take();
                        think(); // pour augmenter la probabilité d'interblocage
                        myLeftStick.take();
                }
                // Si on arrive ici, on a pu "take" les 2 baguettes
                eat();
                // On libère les baguettes :
                myLeftStick.release();
                myRightStick.release();
                // try again
            } catch (InterruptedException ex) {
                Logger.getLogger("Table").log(Level.SEVERE, "{0} Interrupted", this.getName());
            }
        } */

         Random random = new Random();
    while (running) {
        try {
            think();

            boolean hasLeft = false;
            boolean hasRight = false;

            while (running) {
                synchronized (myLeftStick) {
                    if (myLeftStick.isFree()) {
                        myLeftStick.take();
                        hasLeft = true;
                    }
                }

                synchronized (myRightStick) {
                    if (myRightStick.isFree()) {
                        myRightStick.take();
                        hasRight = true;
                    }
                }

                if (hasLeft && hasRight) {
                    // Les deux baguettes sont prises : on peut manger
                    eat();
                    myLeftStick.release();
                    myRightStick.release();
                    break; // sortir de la boucle et recommencer
                } else {
                    // Échec → relâcher ce qu'on a et attendre un peu
                    if (hasLeft) {
                        myLeftStick.release();
                        hasLeft = false;
                    }
                    if (hasRight) {
                        myRightStick.release();
                        hasRight = false;
                    }
                    Thread.sleep(random.nextInt(500)); // attendre un peu avant de réessayer
                }
            }

        } catch (InterruptedException ex) {
            Logger.getLogger("Table").log(Level.SEVERE, "{0} Interrupted", this.getName());
        }
    }
    }
    
    // Permet d'interrompre le philosophe "proprement" :
    // Il relachera ses baguettes avant de s'arrêter
    public void leaveTable() {
        running = false;
    }

}
