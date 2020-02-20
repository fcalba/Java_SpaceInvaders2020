
package codigo;

import java.awt.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author albafc
 */
public class Marciano {
    
    public Image imagen1 = null;
    public Image imagen2 = null;
    
    public int posX = 0;
    public int posY = 0;
    
    private int anchoPantalla;
    
    public int vida = 50;
    
    public Marciano(int _anchoPantalla){
        anchoPantalla = _anchoPantalla;
        
    }
    //metodo para mover la nave
    public void mueve(boolean direccion) {
        if (direccion) {
            posX++;
        } else {
            posX--;
        }
    }
}


