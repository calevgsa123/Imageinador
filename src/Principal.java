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
		favoritos.setBackground(Color.PINK);
		favoritos.setPreferredSize(new Dimension(100,100));
		favoritos.setLayout(null);
		add(favoritos,BorderLayout.WEST);
		favoritosCont = new JPanel();
		favoritosCont.setLayout(null);
		favoritos.add(favoritosCont);
		barrascroll = new JScrollBar();
		favoritos.add(barrascroll);
		
		//Panel de imagenes miniatura
		JPanel miniaturas = new JPanel();
		miniaturas.setBackground(Color.WHITE);
		add(miniaturas,BorderLayout.SOUTH);		
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
				incrementoF=0;
				File[] archivos = dialogCarpeta.getSelectedFile().listFiles();
				for(File iA:archivos) {
					if(iA.isFile()) {
						//System.out.println("Archivo: " + iA.getName());
						if(EsImagen(iA.getName())) {
							//System.out.println("simon");
						}
					}else {
						//System.out.println("Directorio: " + iA.getName());
						System.out.println(iA.getName());
						crearFavoritos(iA.getName());
						incrementoF++;
					}
				}
				terminaSelect();
			}
		}
	}
	
	public void crearFavoritos(String directorio) {
		nombrefavoritos=directorio;
		carpetaImg carpetaFavoritos = new carpetaImg();
		carpetaFavoritos.addActionListener(new favoritosClick());
		favoritosCont.add(carpetaFavoritos);
		carpetaFavoritos.setLocation(0, incrementoF*100);
	}
	class carpetaImg extends JButton{
		public carpetaImg() {
			setSize(100,100);
			setToolTipText(nombrefavoritos);
			//extraerImagenDirectorio();
		}
		public void paintComponent(Graphics g) {
			
			try {
			//imagenFolder = ImageIO.read(new File("src/img/folder.png"));
				imagenFolder = extraerImagenDirectorio();
			}finally {}
			super.paintComponent(g);
			g.drawImage(imagenFolder, 0, 0,100,100, null);
			
		}
	}
	public void terminaSelect() {
		favoritos.setSize(100,favoritos.getParent().getHeight()-45);
		favoritosCont.setBounds(0, 0, 100, incrementoF*100);
		if(favoritos.getHeight()<favoritosCont.getHeight()) {
			favoritos.setSize(120,favoritos.getParent().getHeight()-45);
			barrascroll.setBounds(100, 0, 20, favoritos.getHeight());
			barrascroll.addAdjustmentListener(new ScrollingBar());
			barrascroll.setMinimum(0);
			barrascroll.setMaximum(favoritosCont.getHeight()-favoritos.getHeight());
			barrascroll.setValue(0);
			barrascroll.setUnitIncrement(100);
		}	
		
	}
	
	public class ScrollingBar implements AdjustmentListener {
		public void adjustmentValueChanged(AdjustmentEvent e) {
			e.getAdjustable().setBlockIncrement(100);
			favoritosCont.setLocation(0, -e.getValue());
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
	
	public Image extraerImagenDirectorio ()  {
			Image imagen= null;
			boolean imagenB=true;
			File directorio = new File(directorioPrincipal + "\\" + nombrefavoritos );
			if(directorio.list()!=null) {
				for(String iA:directorio.list()) {
					if(EsImagen(iA)) {
						try {
							imagen = ImageIO.read(new File(directorioPrincipal + "\\" + nombrefavoritos + "\\" + iA ));
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
			return imagen;
		}
	private String directorioPrincipal;
	private JPanel favoritos;
	private JPanel favoritosCont;
	private JScrollBar barrascroll;
	Box cajaVertical;
	Image imagenFolder;
	int incrementoF=0;
	private String nombrefavoritos;
}
 