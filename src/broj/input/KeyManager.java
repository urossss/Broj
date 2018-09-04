package broj.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

public class KeyManager implements KeyListener {
    
    public static boolean[] numberKeys, keys;

    public KeyManager() {
        numberKeys = new boolean[10];
        keys = new boolean[256];
    }
    
    public void tick() {
        numberKeys[0] = keys[KeyEvent.VK_0];
        numberKeys[1] = keys[KeyEvent.VK_1];
        numberKeys[2] = keys[KeyEvent.VK_2];
        numberKeys[3] = keys[KeyEvent.VK_3];
        numberKeys[4] = keys[KeyEvent.VK_4];
        numberKeys[5] = keys[KeyEvent.VK_5];
        numberKeys[6] = keys[KeyEvent.VK_6];
        numberKeys[7] = keys[KeyEvent.VK_7];
        numberKeys[8] = keys[KeyEvent.VK_8];
        numberKeys[9] = keys[KeyEvent.VK_9];
        
        Arrays.fill(keys, false);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("click");
        keys[e.getKeyCode()] = true;
        tick();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
        tick();
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        
    }

}
