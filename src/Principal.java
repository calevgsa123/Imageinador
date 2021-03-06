import javax.imageio.ImageIO;
import javax.swing.JOptionPane.*;
import javax.swing.border.LineBorder;
import javax.swing.*;
import javax.swing.Box.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

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
		//menu.setBackground(Color.MAGENTA);
		add(menu,BorderLayout.NORTH);
		
		//---------------------------------------------Crea la barra de menus-------------------------------------
		//Boton Seleccionar
		JButton selectFolder = new JButton("Select");
		selectFolder.setToolTipText("Selecciona el folder donde tienes todas las imagenes que quieres ordenar y renombrar");
		selectFolder.addActionListener(new selectFolder_click());
		menu.add(selectFolder);
		JButton listaIMG = new JButton("Lista");
		listaIMG.setToolTipText("Muesta una ventana con la lista de imagenes mostrando solo el nombre");
		listaIMG.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
			}});
		menu.add(listaIMG);
		anterior = new JButton("<");
		anterior.setEnabled(false);
		anterior.setToolTipText("Selecciona la imagen anterion a la que estas situado (F5)");
		anterior.addActionListener(new clickBack());
		menu.add(anterior);
		nombreIMG = new JTextField(20);
		nombreIMG.setEnabled(false);
		menu.add(nombreIMG);
		siguiente = new JButton(">");
		siguiente.setToolTipText("Muestra la imagen siguiente (F6)");
		siguiente.addActionListener(new cliclForward());
		siguiente.setEnabled(false);
		menu.add(siguiente);
		renombrar = new JButton("�");
		renombrar.setToolTipText("Renombra la imagen con el nombre que acabas de escribir (Enter)");
		renombrar.addActionListener(new renombrar_click());
		renombrar.setEnabled(false);
		menu.add(renombrar);
		eliminar = new JButton("X");
		eliminar.setToolTipText("Elimina la imagen (Supr)");
		eliminar.setBackground(Color.RED);
		eliminar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				eliminarImg();
			}
			
		});
		eliminar.setEnabled(false);
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
		visorCont = new JLabel();
		visor = new JScrollPane(visorCont);
		visor.setBackground(Color.CYAN);
		visorCont.setHorizontalAlignment(JLabel.CENTER);
		try {
			visorCont.setIcon(new ImageIcon(ImageIO.read(new File("src/img/imagenador.jpg"))));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		add(visor,BorderLayout.CENTER);
		
		nombreIMG.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				nombreIMG.selectAll();
				
			}

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
		});
		nombreIMG.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar()=='\\') {
					Toolkit.getDefaultToolkit().beep();
					nombreIMG.setBackground(Color.RED);
				}
				if(e.getKeyChar()=='/') {
					Toolkit.getDefaultToolkit().beep();
					nombreIMG.setBackground(Color.RED);
				}
				if(e.getKeyChar()==':') {
					Toolkit.getDefaultToolkit().beep();
					nombreIMG.setBackground(Color.RED);
				}
				if(e.getKeyChar()=='*') {
					Toolkit.getDefaultToolkit().beep();
					nombreIMG.setBackground(Color.RED);
				}
				if(e.getKeyChar()=='?') {
					Toolkit.getDefaultToolkit().beep();
					nombreIMG.setBackground(Color.RED);
				}
				if(e.getKeyChar()=='\"') {
					Toolkit.getDefaultToolkit().beep();
					nombreIMG.setBackground(Color.RED);
				}
				if(e.getKeyChar()=='<') {
					Toolkit.getDefaultToolkit().beep();
					nombreIMG.setBackground(Color.RED);
				}
				if(e.getKeyChar()=='>') {
					Toolkit.getDefaultToolkit().beep();
					nombreIMG.setBackground(Color.RED);
				}
				
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyCode()==KeyEvent.VK_ENTER) {
					File archivo2 = new  File(directorioPrincipal + "\\" + nombreIMG.getText()+extencion);
					if(archivo2.exists()) {
						JOptionPane.showMessageDialog(Form.this, "La imagen con este nombre ya existe");
					}else {
						renombrarIMG();
						int indiceB = miniaturasCont.getComponentZOrder(botonSelectIMG);
						if((indiceB+1)<miniaturasCont.getComponents().length) {
						botonSelectIMG=(JButton)miniaturasCont.getComponent(indiceB+1);
						miniaturaClick t=new miniaturaClick();
						t.actionPerformed(new ActionEvent(botonSelectIMG,0,""));
						nombreIMG.selectAll();
						Toolkit.getDefaultToolkit().beep();
						}
					}
				}
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				switch(e.getKeyCode()) {
				case KeyEvent.VK_F6:
					int indiceB = miniaturasCont.getComponentZOrder(botonSelectIMG);
					if(indiceB<miniaturasCont.getComponents().length-1) {
					botonSelectIMG=(JButton)miniaturasCont.getComponent(indiceB+1);
					miniaturaClick t=new miniaturaClick();
					t.actionPerformed(new ActionEvent(botonSelectIMG,0,""));
					}
					nombreIMG.selectAll();
					break;
				case KeyEvent.VK_F5:
					int indiceB1 = miniaturasCont.getComponentZOrder(botonSelectIMG);
					if(indiceB1>0) {
					botonSelectIMG=(JButton)miniaturasCont.getComponent(indiceB1-1);
					miniaturaClick t1=new miniaturaClick();
					t1.actionPerformed(new ActionEvent(botonSelectIMG,0,""));
					}
					nombreIMG.selectAll();
					break;
				case KeyEvent.VK_F8:
					eliminarImg();
					break;
				}
				if(e.getKeyCode()==8 || e.getKeyCode()==127) {
					int ce =0;
					for(int i=0;i<nombreIMG.getText().length();i++) {
						char ch = nombreIMG.getText().charAt(i);
						if(ch=='\\'||ch=='/'||ch==':'||ch=='*'||ch=='?'||ch=='\"'||ch=='<'||ch=='>') {
							ce++;
						}
					}
					if (ce==0)
					nombreIMG.setBackground(Color.WHITE);
				}
			}
			
		});
	}
	
	public void renombrarIMG() {
		String nombreImg = directorioPrincipal + "\\" + selectedIMG;
		File archivo = new  File(nombreImg);
		File archivo2 = new  File(directorioPrincipal + "\\" + nombreIMG.getText()+extencion);
		if(archivo2.exists()) {
			JOptionPane.showMessageDialog(Form.this, "La imagen con este nombre ya existe");
		}else {
		archivo.renameTo(archivo2);
		selectedIMG =nombreIMG.getText()+extencion;
		botonSelectIMG.setToolTipText(nombreIMG.getText()+extencion);
		}
	}
	
	public class progreso extends JFrame {
		public progreso() {
			setTitle("Espere...");
			barra = new JProgressBar(0,0);
			setLayout(new BorderLayout());
			setAlwaysOnTop(true);
			incrementoG=0;
			Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
			setBounds((pantalla.width/2)-150, (pantalla.height/2)-80, 400, 80);
			barra.setStringPainted(true);
			barra.setValue(incrementoG);
			add(barra,BorderLayout.CENTER);
			barra.repaint();
			barra.revalidate();
		}

	}
	
	class renombrar_click implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			renombrarIMG();
			focusText();
		}
		
	}
	public class selectFolder_click implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			//Ejecuta la ventana de seleccion de directorio
			JFileChooser dialogCarpeta = new JFileChooser();
			dialogCarpeta.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int respuesta = dialogCarpeta.showOpenDialog(Form.this);
			if (respuesta==JFileChooser.APPROVE_OPTION) {//JFileChooser.APPROVE_OPTION
				File directorio =dialogCarpeta.getSelectedFile();
				directorioPrincipal=directorio.getPath();
				favoritosCont.removeAll();
				miniaturasCont.removeAll();
				incrementoF=0;
				incrementoI=0;
				incrementoG=0;
				arrayImg.clear();
				arrayDir.clear();
				imageList.clear();
				dirList.clear();
				File[] archivos = dialogCarpeta.getSelectedFile().listFiles();
				//---------Crea una ventana con una barra de progreso
				contador = new progreso();
				contador.setVisible(true);
				barra.setMaximum(archivos.length);
				//----------termina de crear la venta
				 //For de carga de imagenes y favoritos
				for(File iA:archivos) {
					if(iA.isFile()) {
						//System.out.println("Archivo: " + iA.getName());
						if(EsImagen(iA.getName())) {
							//crearImagenes(iA.getName());
							arrayImg.add(iA.getName());
							incrementoI++;
						}
					}else {
						//System.out.println(iA.getName());
						//crearFavoritos(iA.getName());
						arrayDir.add(iA.getName());
						incrementoF++;
					}
				}
				if(incrementoI==0 && incrementoF==0) {
					contador.dispose();
					JOptionPane.showMessageDialog(Form.this, "Esta carpeta esta vacia");
				}else if(incrementoI==0) {
					contador.dispose();
					JOptionPane.showMessageDialog(Form.this, "Esta carpeta esta vacia");
				}else {
					cargarImgMin();
				//terminaSelect();
				//cargarImagenPrincipal();
				}
				
			}
		}
	}
	
	public void crearImagenes(String img) {
		imagenB miniatura = new imagenB(img);
		miniatura.setLocation(incrementoI*100, 0);
		miniatura.addActionListener(new miniaturaClick());
		/*botonconimagen r = new botonconimagen(miniatura,img);
		Thread t=new Thread(r);
		t.start();*/
		miniaturasCont.add(miniatura);
	}
	
	class imagenB extends JButton{
		public imagenB(String img) {
			setSize(100,100);
			setToolTipText(img);
		}
	}
	
	public void crearFavoritos(String directorio) {
		carpetaImg carpetaFavoritos= new carpetaImg(directorio);
		carpetaFavoritos.addActionListener(new favoritosClick());
		carpetaFavoritos.setLocation(0, incrementoF*100);
		favoritosCont.add(carpetaFavoritos);
	}
	class carpetaImg extends JButton{
		public carpetaImg(String nomFavoritos) {
			setSize(100,100);
			setToolTipText(nomFavoritos);
		}
	}
	public void terminaSelect() {
		if(incrementoF>0) {
			favoritosCont.setBounds(0, 0, 100, favoritosCont.getComponents().length*100);
			if(favoritos.getHeight()<(favoritosCont.getComponents().length*100)) {
				favoritos.setPreferredSize(new Dimension(120,favoritos.getParent().getHeight()-155));
				barrascroll.setBounds(100, 0, 20, favoritos.getHeight());
				barrascroll.addAdjustmentListener(new ScrollingBar());
				barrascroll.setMinimum(0);
				barrascroll.setMaximum(favoritosCont.getHeight()-favoritos.getHeight());
				barrascroll.setValue(0);
				barrascroll.setUnitIncrement(100);
			}	else
				favoritos.setPreferredSize(new Dimension(100,favoritos.getParent().getHeight()-155));
			
		}else {
			favoritos.setPreferredSize(new Dimension(0,favoritos.getParent().getHeight()-155));
		}
		miniaturasCont.setSize(miniaturasCont.getComponents().length*100, 20);
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
		miniaturasCont.setSize(miniaturasCont.getComponents().length*100, 100);
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
			miniaturasCont.setLocation(-e.getValue(), 0);
		}
		
	}
	public class favoritosClick implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			JButton boton = (JButton)e.getSource();
			File imagen = new File(directorioPrincipal+selectedIMG);
			Path origenPath=FileSystems.getDefault().getPath( directorioPrincipal+"\\"+selectedIMG);
			Path destinoPath=FileSystems.getDefault().getPath(directorioPrincipal+"\\"+boton.getToolTipText()+"\\"+selectedIMG);
			try {
				Files.move(origenPath,destinoPath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			int i=miniaturasCont.getComponentZOrder(botonSelectIMG);
			int i1=miniaturasCont.getComponents().length -1;
			System.out.print("botonborrar");
			miniaturasCont.remove(botonSelectIMG);
			reacomodarMiniaturas();
			if(i==i1) {
				miniaturaClick  g= new miniaturaClick();
				g.actionPerformed(new ActionEvent(miniaturasCont.getComponent(i1-1),0,""));
			}else if( i>=0 && i<i1) {
				miniaturaClick  g= new miniaturaClick();
				g.actionPerformed(new ActionEvent(miniaturasCont.getComponent(i),0,""));
			}
			focusText();
		}
		
	}
	public void focusText() {
		nombreIMG.requestFocus();
		nombreIMG.selectAll();
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
		switch (extencion.toLowerCase()) {
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
		case ".tif":
			imagen = true;
			break;
		case ".jfif":
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
		if(miniaturasCont.getComponents().length >0) {
		JButton boton = (JButton)miniaturasCont.getComponent(0);
		try {
			Image imagen = ImageIO.read(new File(directorioPrincipal+"\\"+boton.getToolTipText()));
			visorCont.setIcon(new ImageIcon(imagen));
		} catch (IOException e) {
			e.printStackTrace();
		}
		barrascroll.repaint();
		selectedIMG=boton.getToolTipText();
		botonSelectIMG=boton;
		botonViejo=boton;
		botonViejo.setBorder(new LineBorder(Color.GREEN,5));
		nombreIMG.setText(extraerNombre(boton.getToolTipText()));
		nombreIMG.requestFocus();
		anterior.setEnabled(true);
		nombreIMG.setEnabled(true);
		siguiente.setEnabled(true);
		renombrar.setEnabled(true);
		eliminar.setEnabled(true);
		}else {
			anterior.setEnabled(false);
			nombreIMG.setEnabled(false);
			siguiente.setEnabled(false);
			renombrar.setEnabled(false);
			eliminar.setEnabled(false);
		}
	}
	
	class miniaturaClick implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton boton = (JButton)e.getSource();
			try {
				Image imagen = ImageIO.read(new File(directorioPrincipal+"\\"+boton.getToolTipText()));
				visorCont.setIcon(new ImageIcon(imagen));
				
			} catch (IOException er) {
				er.printStackTrace();
			}
			favoritos.repaint();
			selectedIMG=boton.getToolTipText();
			botonViejo.setBorder(UIManager.getBorder("Button.border"));
			botonSelectIMG=boton;
			botonSelectIMG.setBorder(new LineBorder(Color.GREEN,5));
			nombreIMG.setText(extraerNombre(boton.getToolTipText()));
			botonViejo=boton;
			focusText();
		}
	}
	
	public void reacomodarMiniaturas() {
		for(int i=0 ;i<miniaturasCont.getComponents().length;i++) {
			miniaturasCont.getComponent(i).setLocation(i*100, 0);
			miniaturasCont.setSize((i+1)*100, 100);
		}
		miniaturasCont.repaint();
	}
	
	public String extraerNombre(String nom) {
		String respuesta =null;
		for(int i=nom.length()-1;i>0;i--) {
			if(nom.charAt(i)=='.') {
				respuesta= nom.substring(0,i);
				extencion= nom.substring(i,nom.length());
				break;
			}
		}
		return respuesta;
	}
	class clickBack implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			int indiceB = miniaturasCont.getComponentZOrder(botonSelectIMG);
			if (indiceB>0) {
				botonSelectIMG=(JButton)miniaturasCont.getComponent(indiceB-1);
				miniaturaClick t=new miniaturaClick();
				t.actionPerformed(new ActionEvent(botonSelectIMG,0,""));
			}
		}
		
	}
	
	class cliclForward implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			int indiceB = miniaturasCont.getComponentZOrder(botonSelectIMG);
			if (indiceB<miniaturasCont.getComponents().length-1) {
				botonSelectIMG=(JButton)miniaturasCont.getComponent(indiceB+1);
				miniaturaClick t=new miniaturaClick();
				t.actionPerformed(new ActionEvent(botonSelectIMG,0,""));
			}
		}
		
	}
	public void eliminarImg() {
		int r=JOptionPane.showConfirmDialog(Form.this, "Quieres eliminar esta imagen: "+selectedIMG,"Eliminar",JOptionPane.YES_NO_OPTION);
		if(r==JOptionPane.OK_OPTION) {
			File archivo = new File(directorioPrincipal+"\\"+selectedIMG);
			archivo.delete();
			int i=miniaturasCont.getComponentZOrder(botonSelectIMG);
			int i1=miniaturasCont.getComponents().length -1;
			miniaturasCont.remove(botonSelectIMG);
			reacomodarMiniaturas();
			if(i==i1) {
				miniaturaClick  g= new miniaturaClick();
				g.actionPerformed(new ActionEvent(miniaturasCont.getComponent(i1-1),0,""));
			}else if( i>=0 && i<i1) {
				miniaturaClick  g= new miniaturaClick();
				g.actionPerformed(new ActionEvent(miniaturasCont.getComponent(i),0,""));
			}
			focusText();
		}
	}
	
	private void cargarImgMin() {
		for(int i =0; i< arrayImg.size();i++) {
			cargarImgRun r = new cargarImgRun(arrayImg.get(i));
			Thread t=new Thread(r);
			t.start();
			
		}
		for(int i =0; i< arrayDir.size();i++) {
			cargarDirRun r = new cargarDirRun(arrayDir.get(i));
			Thread t=new Thread(r);
			t.start();
			
		}
	}
	class cargarDirRun implements Runnable{
		private cargarDirRun (String nombre) {
			this.nombre=nombre;
		}
		@Override
		public void run() {
			Image imagen= null;
			boolean imagenB=true;
			File directorio = new File(directorioPrincipal + "\\" + nombre );
			if(directorio.list()!=null) {
				for(String iA:directorio.list()) {
					if(EsImagen(iA)) {
						lock.lock();
						try {
							imagen = ImageIO.read(new File(directorioPrincipal + "\\" + nombre + "\\" + iA ));
							imagenB=false;
							break;
							}catch(IOException e) {System.out.println("No img");}finally {lock.unlock();}
					}
				}
			}
			if (imagenB) {
				try {
					imagen = ImageIO.read(new File("src/img/folder.png"));
					}catch(IOException e) {System.out.println("No img");}
			}
			Image otra = imagen.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
			dirList.put(nombre, otra);
			
			if(incrementoTmp2==arrayDir.size()) {
				terminaCargarDirRun();//terminaSelect();
			}
			incrementoTmp2++;
		}
		private String nombre;
	}
	class cargarImgRun implements Runnable{
		private cargarImgRun (String nombre) {
			this.nombre=nombre;
		}

		@Override
		public void run() {
			Image imagen=null;
			lock.lock();
			try {
				imagen = ImageIO.read(new File(directorioPrincipal + "\\" + nombre ));
					}catch(IOException e) {System.out.println("No img");}finally {lock.unlock();}
			Image otra = imagen.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
			imageList.put(nombre,otra);
			incrementoTmp++;
			if(incrementoTmp==arrayImg.size()) {
				terminaCargarImgRun();//terminaSelect();
			}
			
		}
		private String nombre;
		
	}
	private void terminaCargarImgRun() {
		/*do
		{System.out.println(imageList.size());
		System.out.println(arrayImg.size());}while(imageList.size()!= arrayImg.size());*/
		for (int i=0;i<arrayImg.size();i++) {
		String	img=arrayImg.get(i);
		imagenB miniatura = new imagenB(img);
		miniatura.setLocation(i*100, 0);
		miniatura.addActionListener(new miniaturaClick());
		
		
		miniatura.setIcon(new ImageIcon(imageList.get(img)));
		
		miniaturasCont.add(miniatura);
		incrementoG++;
		llenarBarra(incrementoG);
		}
	}
	
	private void terminaCargarDirRun() {
		/*do
		{System.out.println(arrayDir.size());
		System.out.println(dirList.size());}while(arrayDir.size()!= dirList.size());*/
		for(int i=0;i<arrayDir.size();i++) {
			String dir = arrayDir.get(i);
			carpetaImg carpetaFavoritos= new carpetaImg(dir);
			carpetaFavoritos.addActionListener(new favoritosClick());
			carpetaFavoritos.setLocation(0, i*100);
			
			
			carpetaFavoritos.setIcon(new ImageIcon(dirList.get(dir)));
			
			favoritosCont.add(carpetaFavoritos);
			incrementoG++;
			llenarBarra(incrementoG);
		}
	}
	
	private void llenarBarra(int valor) {
		barra.setValue(incrementoG);
		barra.repaint();
		if(incrementoG==barra.getMaximum()) {
			contador.dispose();
			terminaSelect();
			cargarImagenPrincipal();
		}
	}
	private String directorioPrincipal;
	private JPanel favoritos;
	private JPanel favoritosCont;
	private JScrollBar barrascroll;
	private int incrementoF=0;
	private String nombrefavoritos;
	private int incrementoG;
	private JProgressBar barra ;
	private progreso contador;
	private int incrementoI;
	private JPanel miniaturas;
	private JPanel miniaturasCont;
	private JScrollBar barraScrollMin;
	private JScrollPane visor;
	private JLabel visorCont;
	private String selectedIMG;
	private JButton botonSelectIMG;
	private JTextField nombreIMG;
	private JButton botonViejo;
	private String extencion;
	private JButton anterior;
	private JButton siguiente;
	private JButton renombrar;
	private JButton eliminar;
	
	private ArrayList<String> arrayDir = new ArrayList<String>();
	private ArrayList<String> arrayImg = new ArrayList<String>();
	private HashMap<String,Image> imageList= new HashMap<String,Image>();
	private HashMap<String,Image> dirList= new HashMap<String,Image>();
	private int incrementoTmp=1;
	private int incrementoTmp2=1;
	
	  private final ReentrantLock lock = new ReentrantLock();
	  
	  //Reemplazar un archivo existente a la hora de mover a favoritos o renombrar
}
 