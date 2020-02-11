/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Timer;

/**
 *
 * @author jorgecisneros
 */
public class VentanaJuego extends javax.swing.JFrame {

    static int ANCHOPANTALLA = 800;
    static int ALTOPANTALLA = 600;

    int filasMarcianos = 5;
    int columnasMarcianos = 10;
    int contador = 0;

    BufferedImage buffer = null;
    //buffer para guardar 
    BufferedImage plantilla = null;
    Image[] imagenes = new Image[30];

    //bucle de animacion del juego
    //en este caso, es un hilo de ejecucion nuevo que se encarga 
    //de refrescar el contenido de la pantalla
    Timer temporizador = new Timer(10, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO : codigo de la animacion 
            bucleDelJuego();
        }
    });

    Marciano miMarciano = new Marciano(ANCHOPANTALLA);//INICIALIZO
    Marciano[][] listaMarcianos = new Marciano[filasMarcianos][columnasMarcianos];
    boolean direccionMarciano = true;
    Nave miNave = new Nave();
    Disparo miDisparo = new Disparo();

    /**
     * Creates new form VentanaJuego
     */
    public VentanaJuego() {
        
        initComponents();
        
        try {
            plantilla = ImageIO.read(getClass().getResource("/imagenes/invaders2.png"));
        } catch (IOException ex) {
        }
        //cargo la 30 imagenes del sprintesheet en el array de bufferedImages
        for (int i=0; i<5; i++){
            for (int j=0; j<4; j++){
                imagenes[i*4+j] = plantilla.getSubimage(j*64, i*64, 64, 64).getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                
            }
        }
        setSize(ANCHOPANTALLA, ALTOPANTALLA);
        buffer = (BufferedImage) jPanel1.createImage(ANCHOPANTALLA, ALTOPANTALLA);
        buffer.createGraphics();

        //arranco el temporizador para que empiece el juego
        temporizador.start();
        miNave.posX = ANCHOPANTALLA / 2 - miNave.imagen.getWidth(this) / 2;
        miNave.posY = ALTOPANTALLA - 100;
        //creamos el array de marcianos
        for (int i = 0; i < filasMarcianos; i++){
            for (int j = 0; j < columnasMarcianos; j++){
                listaMarcianos[i][j] = new Marciano(ANCHOPANTALLA);
                listaMarcianos[i][j].imagen1 = imagenes[4];
                 listaMarcianos[i][j].imagen2 = imagenes[5];
                listaMarcianos [i][j].posX = j*(15+listaMarcianos[i][j].imagen1.getWidth(null));
                listaMarcianos[i][j].posY = i*(10+listaMarcianos[i][j].imagen1.getHeight(null));
            }

        }
        miDisparo.posY = -2000;
    }
    private void bucleDelJuego() {
        //este metodo gobierna el redibujado de los objetos en el jPanel1

        //primero borro todo lo que hay en el buffer
        Graphics2D g2 = (Graphics2D) buffer.getGraphics();
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, ANCHOPANTALLA, ALTOPANTALLA);

        contador++;
        pintaMarcianos(g2);
        //dibujo
        //////////////////////////////////////////////////////////////////
        
        //dibujo la nave 
        g2.drawImage(miNave.imagen, miNave.posX, miNave.posY, null);
        g2.drawImage(miDisparo.imagen, miDisparo.posX, miDisparo.posY, null);
        miNave.mueve();
        miDisparo.mueve();
        chequeaColision(); 
        
        //////////////////////////////////////////////////////////////////
        //dibujo de golpe todo el buffer sobre el jpanel1
        g2 = (Graphics2D) jPanel1.getGraphics();
        g2.drawImage(buffer, 0, 0, null);
    }
    
    //chequea si un disparo y un marciano colisionan
    private void chequeaColision(){
        Rectangle2D.Double rectanguloMarciano = new Rectangle2D.Double();
        Rectangle2D.Double rectanguloDisparo = new Rectangle2D.Double();
        
        //calculo el rectangulo que contiene al disparo
        rectanguloDisparo.setFrame(miDisparo.posX, miDisparo.posY, miDisparo.imagen.getWidth(null), miDisparo.imagen.getHeight(null));
        
        for (int i=0; i<filasMarcianos; i++){
            for (int j=0; j<columnasMarcianos; j++){
                rectanguloMarciano.setFrame(listaMarcianos[i][j].posX, listaMarcianos[i][j].posY, listaMarcianos[i][j].imagen1.getWidth(null), listaMarcianos[i][j].imagen1.getHeight(null));
                if (rectanguloDisparo.intersects(rectanguloMarciano)){
                    //si entra aqui es porque han chocado un marciano y un disparo
                    listaMarcianos[i][j].posY = 2000;
                    miDisparo.posY = -2000; 
                    
                }
            }
        }
    }
    
    private void pintaMarcianos(Graphics2D _g2){
       
        for (int i = 0; i < filasMarcianos; i++){
            for (int j = 0; j < columnasMarcianos; j++){
                listaMarcianos[i][j].mueve(direccionMarciano);
                if (listaMarcianos[i][j].posX >= ANCHOPANTALLA - listaMarcianos[i][j].imagen1.getWidth(null) || listaMarcianos[i][j].posX <=0){
            direccionMarciano = !direccionMarciano;
            //hago que todos los marcianos salten a la siguiente columna
            for(int k = 0; k < filasMarcianos; k++)
                for (int m = 0; m < columnasMarcianos; m++){
                    listaMarcianos[k][m].posY += listaMarcianos[k][m].imagen1.getHeight(null);
                }
            
        }
                if (contador < 50){
                    _g2.drawImage(listaMarcianos[i][j].imagen1, listaMarcianos[i][j].posX, listaMarcianos[i][j].posY,null);
                }
                else if (contador < 100){
                     _g2.drawImage(listaMarcianos[i][j].imagen2, listaMarcianos[i][j].posX, listaMarcianos[i][j].posY,null);
                }
                else contador = 0;
            }
        }
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        jPanel1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPanel1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPanel1KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 431, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 352, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel1KeyPressed

    private void jPanel1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel1KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel1KeyReleased

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                miNave.setPulsadoIzquierda(true);
                break;
            case KeyEvent.VK_RIGHT : miNave.setPulsadoDerecha(true); break;
            case KeyEvent.VK_SPACE : miDisparo.posicionaDisparo(miNave);
                                     break;
        }
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                miNave.setPulsadoIzquierda(false);
                break;
            case KeyEvent.VK_RIGHT : miNave.setPulsadoDerecha(false); break;

        }
    }//GEN-LAST:event_formKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaJuego().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
