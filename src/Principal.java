import javax.imageio.ImageIO;
import javax.swing.JOptionPane.*;
import javax.swing.*;
import javax.swing.Box.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.swing.ImageIcon;

public class Principal {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Form form1 = new Form();
		form1.setTitle("Imagenador");
		form1.setVisible(true);
		form1.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
	}
	
}
 class Form extends JFrame{

	public Form() {
		//Se Crea la barra de herramientas
		setLayout(new BorderLayout());
		JPanel menu = new JPanel();
		menu.setLayout(new FlowLayout(FlowLayout.LEFT));
		menu.setSize(100, 35);
		menu.setBackground(Color.MAGENTA);
		add(menu,BorderLayout.NORTH);
		
		//---------------------------------------------Crea la barra de menus-------------------------------------
		//Boton Seleccionar
		JButton selectFolder = new JButton("Select");
		selectFolder.setToolTipText("Selecciona el folder donde tienes todas las imagenes que quieres ordenar y renombrar");
		selectFolder.addActionListener(new selectFolder_click());
		menu.add(selectFolder);
		JButton listaIMG = new JButton("Lista");
		listaIMG.setToolTipText("Muesta una ventana con la lista de imagenes mostrando solo el nombre");
		menu.add(listaIMG);
		JButton anterior = new JButton("<");
		anterior.setToolTipText("Selecciona la imagen anterion a la que estas situado ");
		menu.add(anterior);
		JTextField nombreIMG = new JTextField(15);
		menu.add(nombreIMG);
		JButton siguiente = new JButton(">");
		siguiente.setToolTipText("Muestra la imagen siguiente");
		menu.add(siguiente);
		JButton renombrar = new JButton("§");
		renombrar.setToolTipText("Renombra la imagen con el nombre que acabas de escribir (Enter)");
		menu.add(renombrar);
		JButton eliminar = new JButton("X");
		eliminar.setToolTipText("Elimina la imagen (Supr)");
		menu.add(eliminar);
		
		//Panel de carpetas a mover Favoritos
		favoritos = new JPanel();
		favoritos.setBackground(Color.GRAY);
		favoritos.setPreferredSize(new Dimension(100,100));
		favoritos.setLayout(null);
		add(favoritos,BorderLayout.WEST);
		favoritosCont = new JPanel();
		favoritosCont.setLayout(null);
		favoritos.add(favoritosCont);
		barrascroll = new JScrollBar();
		favoritos.add(barrascroll);
		
		//Panel de imagenes miniatura
		miniaturas = new JPanel();
		miniaturas.setBackground(Color.WHITE);
		miniaturas.setPreferredSize(new Dimension(100,120));
		miniaturas.setLayout(null);
		add(miniaturas,BorderLayout.SOUTH);
		miniaturasCont = new JPanel();
		miniaturasCont.setLayout(null);
		//miniaturasCont.setBounds(0, 0, 500, 500);
		miniaturas.add(miniaturasCont);
		barraScrollMin = new JScrollBar(JScrollBar.HORIZONTAL);
		barraScrollMin.setLocation(0, 100);
		barraScrollMin.setBounds(0, 100, 10, 20);
		miniaturas.add(barraScrollMin);
		
		//Imagen principal
		visor = new JPanel();
		visor.setBackground(Color.CYAN);
		add(visor,BorderLayout.CENTER);
		visorCont = new JLabel();
		visor.add(visorCont);
		
	}
	
	public class progreso extends JFrame {
		public progreso() {
			setTitle("Espere...");
			setLayout(new BorderLayout());
			setAlwaysOnTop(true);
			incrementoG=0;
			Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
			setBounds((pantalla.width/2)-150, (pantalla.height/2)-80, 400, 80);
			barra = new JProgressBar(0,0);
			barra.setStringPainted(true);
			barra.setValue(incrementoG);
			add(barra,BorderLayout.CENTER);
		}

	}
	
	public class selectFolder_click implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			//Ejecuta la ventana de seleccion de directorio
			JFileChooser dialogCarpeta = new JFileChooser();
			dialogCarpeta.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int respuesta = dialogCarpeta.showOpenDialog(Form.this);
			if (respuesta==JFileChooser.APPROVE_OPTION) {
				File directorio = dialogCarpeta.getSelectedFile();
				directorioPrincipal=directorio.getPath();
				favoritosCont.removeAll();
				miniaturasCont.removeAll();
				imagenesF.clear();
				incrementoF=0;
				incrementoI=0;
				File[] archivos = dialogCarpeta.getSelectedFile().listFiles();
				//---------Crea una ventana con una barra de progreso
				contador = new progreso();
				contador.setVisible(true);
				barra.setMaximum(archivos.length);
				//----------termina de crear la venta
				
				for(File iA:archivos) {
					if(iA.isFile()) {
						//System.out.println("Archivo: " + iA.getName());
						if(EsImagen(iA.getName())) {
							//System.out.println("simon");
							crearImagenes(iA.getName());
							incrementoI++;
						}
					}else {
						//System.out.println("Directorio: " + iA.getName());
						//System.out.println(iA.getName());
						crearFavoritos(iA.getName());
						incrementoF++;
					}
					incrementoG++;
					barra.setValue(incrementoG);
				}
				
				
				contador.setVisible(false);
				terminaSelect();
				cargarImagenPrincipal();
			}
		}
	}
	
	public void crearImagenes(String img) {
		imagenB miniatura = new imagenB(img);
		miniatura.setLocation(incrementoI*100, 0);
		try {
		Image imagenv = ImageIO.read(new File(directorioPrincipal+"\\"+img));
		Image imagenv2 = imagenv.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
		miniatura.setIcon(new ImageIcon(imagenv2));
		}catch(IOException e) {}
		miniaturasCont.add(miniatura);
	}
	
	class imagenB extends JButton{
		public imagenB(String img) {
			setSize(100,100);
			setToolTipText(img);
		}
	}
	
	public void crearFavoritos(String directorio) {
		nombrefavoritos=directorio;
		carpetaImg carpetaFavoritos= new carpetaImg();
		carpetaFavoritos.addActionListener(new favoritosClick());
		imagenesF.add(extraerImagenDirectorio(directorio));
		carpetaFavoritos.setIcon(new ImageIcon(imagenesF.get(incrementoF)));
		carpetaFavoritos.setLocation(0, incrementoF*100);
		favoritosCont.add(carpetaFavoritos);
	}
	class carpetaImg extends JButton{
		public carpetaImg() {
			setSize(100,100);
			setToolTipText(nombrefavoritos);
		}
	}
	public void terminaSelect() {
		favoritos.setSize(100,favoritos.getParent().getHeight()-155);
		favoritosCont.setBounds(0, 0, 100, favoritosCont.getComponents().length*100);
		if(favoritos.getHeight()<favoritosCont.getHeight()) {
			favoritos.setSize(120,favoritos.getParent().getHeight()-156);
			barrascroll.setBounds(100, 0, 20, favoritos.getHeight());
			barrascroll.addAdjustmentListener(new ScrollingBar());
			barrascroll.setMinimum(0);
			barrascroll.setMaximum(favoritosCont.getHeight()-favoritos.getHeight());
			barrascroll.setValue(0);
			barrascroll.setUnitIncrement(100);
		}	
		miniaturasCont.setSize(miniaturasCont.getComponents().length*100, 100);
		
		if(miniaturasCont.getWidth()>miniaturasCont.getParent().getWidth()) {
			barraScrollMin.setBounds(0, 100, barraScrollMin.getParent().getWidth(), 20);
			barraScrollMin.setMaximum(miniaturasCont.getWidth()-miniaturasCont.getParent().getWidth());
			barraScrollMin.addAdjustmentListener(new ScrollingBarH());
			barraScrollMin.setMinimum(0);
			barraScrollMin.setValue(0);
			barraScrollMin.setUnitIncrement(100);
		}else {
			barraScrollMin.setBounds(0, 100, barraScrollMin.getParent().getWidth(), 20);
			barraScrollMin.setMaximum(0);
			barraScrollMin.setMinimum(0);
			barraScrollMin.setValue(0);
			barraScrollMin.setUnitIncrement(100);
		}
		miniaturasCont.repaint();
	}
	
	public class ScrollingBar implements AdjustmentListener {
		public void adjustmentValueChanged(AdjustmentEvent e) {
			e.getAdjustable().setBlockIncrement(100);
			favoritosCont.setLocation(0, -e.getValue());
		}
	}
	
	public class ScrollingBarH implements AdjustmentListener{

		@Override
		public void adjustmentValueChanged(AdjustmentEvent e) {
			// TODO Auto-generated method stub
			miniaturasCont.setLocation(-e.getValue(), 0);
		}
		
	}
	public class favoritosClick implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			JButton boton = (JButton)e.getSource();
			JOptionPane.showMessageDialog(null, boton.getToolTipText(), "mesage", JOptionPane.DEFAULT_OPTION);
		}
		
	}
	
	public boolean EsImagen(String nombre) {
		String extencion="";
		boolean imagen=false;
		//Extraer la extencion
		for(int i=nombre.length()-1;i>=0;i--) {
			char punto = nombre.charAt(i);
			if (punto=='.') {
				extencion=nombre.substring(i, nombre.length());
				break;
			}
		}
		switch (extencion) {
		case ".jpg":
			imagen = true;
			break;
		case ".jpeg":
			imagen = true;
			break;
		case ".gif":
			imagen = true;
			break;
		case ".bmp":
			imagen = true;
			break;
		case ".png":
			imagen = true;
			break;
		}
		return imagen;
	}
	
	public Image extraerImagenDirectorio (String dirboton)  {
			Image imagen= null;
			boolean imagenB=true;
			File directorio = new File(directorioPrincipal + "\\" + dirboton );
			if(directorio.list()!=null) {
				for(String iA:directorio.list()) {
					if(EsImagen(iA)) {
						try {
							imagen = ImageIO.read(new File(directorioPrincipal + "\\" + dirboton + "\\" + iA ));
							imagenB=false;
							break;
							}catch(IOException e) {System.out.println("No img");}
					}
				}
			}
			if (imagenB) {
				try {
					imagen = ImageIO.read(new File("src/img/folder.png"));
					}catch(IOException e) {System.out.println("No img");}
			}
			Image otra = imagen.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
			return otra;
		}
	
	public void cargarImagenPrincipal() {
		JButton boton = (JButton)miniaturasCont.getComponent(0);
		try {
			Image imagen = ImageIO.read(new File(directorioPrincipal+"\\"+boton.getToolTipText()));
			visorCont.setIcon(new ImageIcon(imagen));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private String directorioPrincipal;
	private JPanel favoritos;
	private JPanel favoritosCont;
	private JScrollBar barrascroll;
	Box cajaVertical;
	Image imagenFolder;
	int incrementoF=0;
	private String nombrefavoritos;
	Image favoritos_imgs[];
	int total_achivos;
	ArrayList<Image> imagenesF = new ArrayList<Image>();
	int incrementoG;
	JProgressBar barra ;
	progreso contador;
	private int incrementoI;
	JPanel miniaturas;
	JPanel miniaturasCont;
	JScrollBar barraScrollMin;
	JPanel visor;
	JLabel visorCont;
}
 