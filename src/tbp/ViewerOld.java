package tbp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import path.Path;
import tbp.Encoding.Stage;
import tbp.TabSymbol.TabSymbolSet;
import tools.ToolBox;

public class ViewerOld extends JFrame {
	private static final long serialVersionUID = 1L;

	private static final Integer[] FRAME_DIMS = new Integer[]{717, 672};
	private static final Integer[] PANEL_DIMS = new Integer[]{586, 413};
	private static final Font FONT = new Font("Courier New", Font.PLAIN, 12);
	private static final String NAME = "TabViewer";
	private static final String EXTENSION = ".tab";
	private static final String[] TITLE = new String[]{"untitled", Encoding.EXTENSION, " - " + NAME};
	public static final String METADATA_ERROR = 
		"METADATA ERROR -- Check for missing or misplaced curly brackets.";
	
	private Highlighter highlighter;
	private JLabel pieceLabel;
	private ButtonGroup tabTypeButtonGroup;
	private JLabel upperErrorLabel;
	private JLabel lowerErrorLabel;
	private JTextArea encodingTextArea;
	private JCheckBox rhythmSymbolsCheckBox;
	private JButton viewButton;	
	private JPanel encodingPanel;
	private JMenuBar encodingMenuBar;
	private JFileChooser fileChooser;
	private File file;

	private JTextArea tabTextArea;
	private JPanel tabPanel;
	private JMenuBar tabMenuBar;


	public static void main(String[] args) {
		new ViewerOld(null, "", true);
	}


	///////////////////////////////
	//
	//  C O N S T R U C T O R S
	//
	public ViewerOld(File file, String content, boolean encodingFrame) {
		super();
		init(file, content, encodingFrame);
	}


	private void init(File file, String content, boolean encodingFrame) {
		if (encodingFrame) {
			setHighlighter();
			setPieceLabel();
			setTabTypeButtonGroup();
			setUpperErrorLabel();
			setLowerErrorLabel();
			setEncodingTextArea();
			setRhythmSymbolsCheckBox();
			setViewButton();
			//
			setEncodingPanel();
			setEncodingMenuBar();
		}
		else {
			setTabTextArea(content);
			//
			setTabPanel();
			setTabMenuBar();
		}
		setJMenuBar(encodingFrame ? getEncodingMenuBar() : getTabMenuBar());
		setContentPane(encodingFrame ? getEncodingPanel() : getTabPanel());
		setFileChooser();
		setFile(file);
		setSize(FRAME_DIMS[0], FRAME_DIMS[1]);		
		setVisible(true);
		setTitle(encodingFrame ? TITLE[0] + TITLE[1] + TITLE[2] : file.getName() + TITLE[2]);
		setDefaultCloseOperation(encodingFrame ? WindowConstants.EXIT_ON_CLOSE : WindowConstants.HIDE_ON_CLOSE);
	}


	//////////////////////////////
	//
	//  S E T T E R S  
	//  for instance variables
	//
	private void setHighlighter() {
		highlighter = new DefaultHighlighter();
	}


	private void setPieceLabel() {
		JLabel l = new JLabel();
		l.setBounds(99, 17, 592, 14);
		pieceLabel = l;
	}


	private void setTabTypeButtonGroup() {
		ButtonGroup bg = new ButtonGroup();
		JRadioButton rb = new JRadioButton(TabSymbolSet.FRENCH.getType());
		rb.setBounds(new Rectangle(99, 42, 106, 16));
		rb.setSelected(true);
		bg.add(rb);
		rb = new JRadioButton(TabSymbolSet.ITALIAN.getType());
		rb.setBounds(new Rectangle(207, 42, 106, 16));
		bg.add(rb);
		rb = new JRadioButton(TabSymbolSet.SPANISH.getType());
		rb.setBounds(new Rectangle(315, 42, 106, 16));
		bg.add(rb);
		rb = new JRadioButton(TabSymbolSet.NEWSIDLER_1536.getType());
		rb.setBounds(new Rectangle(423, 42, 106, 16));
		bg.add(rb);
		tabTypeButtonGroup = bg;
	}


	private void setUpperErrorLabel() {
		JLabel l = new JLabel();
		l.setBounds(new Rectangle(99, 69, 592, 16));
		l.setForeground(Color.RED);
		upperErrorLabel = l;
	}


	private void setLowerErrorLabel() {
		JLabel l = new JLabel();
		l.setBounds(new Rectangle(99, 88, 592, 16));
		l.setForeground(Color.RED);
		lowerErrorLabel = l;
	}


	private void setEncodingTextArea() {
		encodingTextArea = makeTextArea(true, "");
	}


	private JTextArea makeTextArea(boolean encodingFrame, String content) {
		JTextArea ta = new JTextArea();
		ta.setBounds(encodingFrame ? new Rectangle(15, 105, 571, 76) : new Rectangle(15, 240, 571, 136));
		ta.setLineWrap(encodingFrame ? true : false); // necessary because of JScrollPane
		ta.setEditable(encodingFrame ? true : false);
		ta.setFont(FONT);
		ta.setText(content);
		ta.setHighlighter(encodingFrame ? getHighlighter() : null);
		return ta;
	}


	private void setRhythmSymbolsCheckBox() {
		JCheckBox cb = new JCheckBox();
		cb.setBounds(new Rectangle(15, 559, 261, 16));
//		cb.setActionCommand("Show all rhythm symbols");
		cb.setText("Do not show repeated rhythm symbols");
		rhythmSymbolsCheckBox = cb;
	}


	private void setViewButton() {
		JButton b = new JButton();
		b.setBounds(new Rectangle(600, 559, 91, 31));
		b.setText("View");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewButtonAction();
			}
		});
		viewButton = b;
	}


	private void setEncodingPanel() {
		encodingPanel = makePanel(true);
	}


	private JPanel makePanel(boolean encodingFrame) {
		JPanel p = new JPanel();
		p.setLayout(null);
		p.setSize(new Dimension(PANEL_DIMS[0], PANEL_DIMS[1]));

		if (encodingFrame) {
			JLabel l = new JLabel("Piece:");
			l.setBounds(new Rectangle(15, 15, 81, 16));
			p.add(l, null);
			p.add(getPieceLabel());
			//
			l = new JLabel("View as:");
			l.setBounds(new Rectangle(15, 42, 81, 16));
			p.add(l, null);
			for (AbstractButton b : Collections.list(getTabTypeButtonGroup().getElements())) {
				p.add(b, null);
			}
			//
			l = new JLabel("Error:");
			l.setBounds(15, 69, 81, 16);
			p.add(l, null);
			p.add(getUpperErrorLabel(), null);
			p.add(getLowerErrorLabel(), null);
			//
			p.add(getRhythmSymbolsCheckBox(), null);
			//
			p.add(getViewButton(), null);
		}
		JScrollPane sp = 
			new JScrollPane(encodingFrame ? getEncodingTextArea() : getTabTextArea(), 
			ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setBounds(encodingFrame ? new Rectangle(15, 115, 676, 430) : new Rectangle(15, 115-100, 676, 430+100));
		p.add(sp, null);

		return p;
	}


	private void setEncodingMenuBar() {
		encodingMenuBar = makeMenuBar(true);
	}


	private JMenuBar makeMenuBar(boolean encodingFrame) {
		JMenuBar mb = new JMenuBar();
//		JTextArea ta = encodingFrame ? getEncodingTextArea() : getTabTextArea();

		JMenu m = new JMenu("File");
		mb.add(m);
		if (encodingFrame) {
			JMenuItem newMenuItem = new JMenuItem("New");
			newMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					newFileAction();
				}
			});
			m.add(newMenuItem);
			JMenuItem openMenuItem = new JMenuItem("Open");
			openMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					openFileAction();
				}
			});
			m.add(openMenuItem);
			JMenuItem saveMenuItem = new JMenuItem("Save");
			saveMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					saveFileAction(encodingFrame ? Encoding.EXTENSION : EXTENSION);
				}
			});
			m.add(saveMenuItem);
			
		}
		JMenuItem saveAsMenuItem = new JMenuItem("Save as");
		saveAsMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAsFileAction(/*ta.getText(),*/ encodingFrame ? Encoding.EXTENSION : EXTENSION);
			}
		});
		m.add(saveAsMenuItem);
		
		JMenu sm = new JMenu("Import");
		JMenuItem asciiImportSubmenuItem = new JMenuItem("ASCII tab");
		asciiImportSubmenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				importFileAction();
			}
		});
		sm.add(asciiImportSubmenuItem);
		JMenuItem tabCodeSubmenuItem = new JMenuItem("TabCode");
		tabCodeSubmenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				importFileAction();
			}
		});
		sm.add(tabCodeSubmenuItem);
		m.add(sm);
		
		sm = new JMenu("Export");
		JMenuItem asciiSubmenuItem = new JMenuItem("ASCII tab");
		asciiSubmenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportFileAction();
			}
		});
		sm.add(asciiSubmenuItem);
		JMenuItem tabMEISubmenuItem = new JMenuItem("MEI tab");
		tabMEISubmenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportFileAction();
			}
		});
		sm.add(tabMEISubmenuItem);
		m.add(sm);
		
		//
		m = new JMenu("Edit");
		if (encodingFrame) {
			mb.add(m);
			JMenuItem selectAllMenuItem = new JMenuItem("Select all");
			selectAllMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getEncodingTextArea().requestFocus();
					getEncodingTextArea().selectAll();
				}
			});
			m.add(selectAllMenuItem);
		}

		return mb;
	}


	private void setFileChooser() {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(Path.ROOT_PATH_USER + Path.ENCODINGS_PATH));
		fileChooser = fc;
	}


	private void setFile(File f) {
		file = f;
	}


	private void setTabTextArea(String content) {
		tabTextArea = makeTextArea(false, content);
	}


	private void setTabPanel() {
		tabPanel = makePanel(false);
	}


	private void setTabMenuBar() {
		tabMenuBar = makeMenuBar(false);
	}


	//////////////////////////////
	//
	//  G E T T E R S
	//  for instance variables
	//
	private Highlighter getHighlighter() {
		return highlighter;
	}


	private JLabel getPieceLabel() {
		return pieceLabel;
	}


	private ButtonGroup getTabTypeButtonGroup() {
		return tabTypeButtonGroup;
	}


	private JLabel getUpperErrorLabel() {
		return upperErrorLabel;
	}


	private JLabel getLowerErrorLabel() {
		return lowerErrorLabel;
	}


	private JTextArea getEncodingTextArea() {
		return encodingTextArea; 
	}


	private JCheckBox getRhythmSymbolsCheckBox() {
		return rhythmSymbolsCheckBox; 
	}


	private JButton getViewButton() {
		return viewButton;
	}


	private JPanel getEncodingPanel() {
		return encodingPanel;
	}


	private JMenuBar getEncodingMenuBar() {
		return encodingMenuBar;
	}


	private JFileChooser getFileChooser() {
		return fileChooser;
	}


	private File getFile() {
		return file;
	}


	private JTextArea getTabTextArea() {
		return tabTextArea;
	}


	private JPanel getTabPanel() {
		return tabPanel;
	}


	private JMenuBar getTabMenuBar() {
		return tabMenuBar;
	}


	//////////////////////////////////////
	//
	//  I N S T A N C E  M E T H O D S
	//
	private void newFileAction() {
		setTitle(TITLE[0] + TITLE[1] + TITLE[2]);
		setFile(null);
		String s = "";
		for (String t : Encoding.METADATA_TAGS) {
			s += Encoding.OPEN_METADATA_BRACKET + t + ":" + Encoding.CLOSE_METADATA_BRACKET + "\r\n";
		}
		s += Symbol.END_BREAK_INDICATOR;
		getEncodingTextArea().setText(s);
	}


	private void openFileAction() {
		getFileChooser().setDialogType(JFileChooser.OPEN_DIALOG);
		// Set file type filter
		getFileChooser().setFileFilter(new FileNameExtensionFilter("tab+ (.tbp)", 
			Encoding.EXTENSION.substring(1)));
//		int returnValue = getFileChooser().showOpenDialog(this);
		if (getFileChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = null;
			try {
//				getFileChooser().setDialogType(JFileChooser.OPEN_DIALOG);
				System.out.println(Encoding.EXTENSION.substring(1));
				System.out.println("jeeee");

				f = getFileChooser().getSelectedFile();
				BufferedReader br = new BufferedReader(new FileReader(getFileChooser().getSelectedFile()));						
			} catch (IOException e) {
				// 11:11
				// https://www.youtube.com/watch?v=Z8p_BtqPk78
				e.printStackTrace();
			}
			setFile(f);
			setTitle(f.getName() + " - " + NAME);
			getPieceLabel().setText("TODO");
//			fileName = f.getName();
//			String rawEnc = ToolBox.readTextFile(encFile);
			String rawEncoding = "";
			try {
				rawEncoding = ToolBox.readTextFile(f);
				rawEncoding = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			getEncodingTextArea().setText(rawEncoding);
		}

		boolean doThis = false;
		if (doThis) {
			String encPath = null; // was method arg
			if (encPath == null) {
				String prefix = "F:/research/data/annotated/encodings/";
				encPath = prefix;
			
				// Test
//				encPath += "test/testpiece.tbp";
//				encPath += "test/test_get_meter_info.tbp";
//				encPath =  "F:/research/publications/conferences-workshops/2019-ISMIR/paper/tst/tab/3610_033_inter_natos_mulierum_morales_T-rev.tbp";
//				encPath =  "F:/research/publications/conferences-workshops/2019-ISMIR/paper/tst/tab/3618_041_benedictus_from_missa_de_l_homme_arme_morales_T.tbp";
//				encPath =  "F:/research/projects/byrd/tst/il_me_souffit-short.tbp";
//				encPath =  "F:/research/projects/byrd/tst/pleni.tbp";
//				encPath =  "C:/Users/Reinier/Desktop/test-capirola/tab/capirola-1520-et_in_terra_pax.tbp";
			
				// Need to be double-checked
//				encPath += "newsidler-1536_6-mein_einigs_a.tbp";
//				encPath += "Newsidler 1536 - Mein hertz alzeyt hat gross verlangen.tbp";
//				encPath += "Ochsenkun 1558 - Cum Sancto spiritu.tbp";  	
//				encPath += "Barbetta 1582 - Martin menoit.tbp";
//				encPath += "Da Crema 1546 - Il nest plaisir.tbp";
//				encPath += "De Narbaez 1538 - MIlle regres.tbp";
//				encPath += "Heckel 1562 - Il est vne Fillete. [Tenor].tbp";
//				encPath += "Heckel 1562 - Il estoit vne fillete. Discant.tbp";
//				encPath += "Morlaye 1552 - LAs on peut iuger.tbp";
//				encPath += "Newsidler 1544 - Der hupff auf.tbp";
//				encPath += "Newsidler 1544 - Hie volget die Schlacht vor Bafia. Der Erst Teyl.tbp";
//				encPath += "Newsidler 1544 - Hie volget die Schlacht vor Bafia. Der ander Teyl der schlacht.tbp";
//				encPath += "Newsidler 1544 - Sula Bataglia.tbp";
//				encPath += "Ochsenkun 1558 - Benedicta es coelorum, Prima pars.tbp";
//				encPath += "Ochsenkun 1558 - Gott alls in allem wesentlich.tbp";
//				encPath += "Ochsenkun 1558 - Pater Noster, Prima pars.tbp"; 
//				encPath += "Ochsenkun 1558 - Praeter rerum seriem, Prima pars.tbp";
//				encPath += "Ochsenkun 1558 - Stabat mater dolorosa, Prima pars.tbp";
//				encPath += "Phalese 1546 - Martin menuyt de Iennequin.tbp";
//				encPath += "Spinacino 1507 - LA Bernardina de Iosquin.tbp";
			
				// Checked and ready for processing
				// 3vv
//				encPath += "thesis-int/3vv/" + "newsidler-1536_7-disant_adiu.tbp";
//				encPath += "thesis-int/3vv/" + "newsidler-1536_7-mess_pensees.tbp";
//				encPath += "thesis-int/3vv/" + "pisador-1552_7-pleni_de.tbp"; // TODO remove every second barline
//				encPath += "thesis-int/3vv/" + "judenkuenig-1523_2-elslein_liebes.tbp";
//				encPath += "thesis-int/3vv/" + "newsidler-1544_2-nun_volget.tbp"; // TODO remove every second barline in ternary part
				encPath += "thesis-int/3vv/" + "phalese-1547_7-tant_que-3vv.tbp";

				// 4vv
//				encPath += "thesis-int/4vv/" + "ochsenkun-1558_5-absolon_fili.tbp";
//				encPath += "thesis-int/4vv/" + "ochsenkun-1558_5-in_exitu.tbp";
//				encPath += "thesis-int/4vv/" + "ochsenkun-1558_5-qui_habitat.tbp";
//				encPath += "thesis-int/4vv/" + "rotta-1546_15-bramo_morir.tbp";
//				encPath += "thesis-int/4vv/" + "phalese-1547_7-tant_que-4vv.tbp";
//				encPath += "thesis-int/4vv/" + "ochsenkun-1558_5-herr_gott.tbp";
//				encPath += "thesis-int/4vv/" + "abondante-1548_1-mais_mamignone.tbp";
//				encPath += "thesis-int/4vv/" + "phalese-1563_12-las_on.tbp";
//				encPath += "thesis-int/4vv/" + "barbetta-1582_1-il_nest.tbp"; // TODO remove every second barline
//				encPath += "thesis-int/4vv/" + "barbetta-1582_1-il_nest-corrected.tbp"; // TODO remove every second barline
//				encPath += "thesis-int/4vv/" + "phalese-1563_12-il_estoit.tbp";
//				encPath += "thesis-int/4vv/" + "BSB-mus.ms._272-mille_regres.tbp";

				// 5vv
//				encPath += "thesis-int/5vv/" + "adriansen-1584_6-d_vn_si.tbp";
//				encPath += "thesis-int/5vv/" + "ochsenkun-1558_5-inuiolata_integra.tbp";
				
				// Byrd
//				encPath += "byrd-int/4vv/ah_golden_hairs-NEW.tbp";

				// JosquIntab
//				encPath = "F:/research/data/annotated/josquintab/tab/" + "5256_05_inviolata_integra_desprez-2.tbp";
//				encPath = "F:/research/data/annotated/josquintab/tab/" + "5263_12_in_exitu_israel_de_egipto_desprez-3.tbp";
//				encPath = "F:/research/data/annotated/josquintab/tab/" + "4465_33-34_memor_esto-2XXX.tbp";
			}
		
			File encFile = new File(encPath);
//			setFile(encFile);
		
			getPieceLabel().setText(encFile.getName());
			String rawEncoding = "";
			try {
				rawEncoding = new String(Files.readAllBytes(Paths.get(encFile.getAbsolutePath())));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			getEncodingTextArea().setText(rawEncoding);
		}
	}


	private void saveFileAction(String extension) {
		String content = 
			extension.equals(Encoding.EXTENSION) ? getEncodingTextArea().getText() : 
			getTabTextArea().getText();
		// New file: treat as Save As
		if (getFile() == null) {
			saveAsFileAction(extension);
		}
		// Existing file
		else {
			ToolBox.storeTextFile(content, getFile());
		}
	}


	private void saveAsFileAction(String extension) {
		String content = 
			extension.equals(Encoding.EXTENSION) ? getEncodingTextArea().getText() : 
			getTabTextArea().getText();

		getFileChooser().setDialogType(JFileChooser.SAVE_DIALOG);
		// Set file type filter and suggested file name 
		if (extension.equals(Encoding.EXTENSION)) {
			getFileChooser().setFileFilter(new FileNameExtensionFilter("tab+ (.tbp)", extension.substring(1)));
			getFileChooser().setSelectedFile(getFile() == null ? new File("untitled" + extension): getFile());
		}
		else {
			getFileChooser().setFileFilter(new FileNameExtensionFilter("ASCII (.tab)", extension.substring(1)));			
			getFileChooser().setSelectedFile(new File(getFile().getAbsolutePath().replace(Encoding.EXTENSION, extension)));
		}
		// https://stackoverflow.com/questions/17010647/set-default-saving-extension-with-jfilechooser
		if (getFileChooser().showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = null;
//			String extension = encodingFrame ? Encoding.EXTENSION.substring(1) : ".tab";
//			try {
			
//				getFileChooser().setDialogType(JFileChooser.SAVE_DIALOG);
//				getFileChooser().setFileFilter(new FileNameExtensionFilter(null, 
//					Encoding.EXTENSION.substring(1)));
				
				
				f = getFileChooser().getSelectedFile();
				System.out.println(f);
//				System.out.println(f);
//				System.out.println(extension);
//				BufferedReader br = new BufferedReader(new FileReader(getFileChooser().getSelectedFile()));
//				BufferedReader br = new BufferedReader(new FileReader(new File("")));

//			} catch (IOException e) {
//				e.printStackTrace();
//			}
				
			if (getFile() == null) {
				setFile(f);
				setTitle(f.getName());
				System.out.println("rrr");
				System.out.println(file);
			}

			// Handle any returns added to encoding (which will be "\n" and not "\r\n") by 
			// replacing them with "\r\n"
			// 1. List all indices of the \ns not preceded by \rs
			List<Integer> indicesOfLineBreaks = new ArrayList<Integer>(); 
			for (int i = 0; i < content.length(); i++) {
				String currentChar = content.substring(i, i + 1);
				if (currentChar.equals("\n")) {
					// NB: previousChar always exists as the char at index 0 in encoding will
					// never be a \n
					String previousChar = content.substring(i - 1, i);	
					if (!previousChar.equals("\r")) {
						indicesOfLineBreaks.add(i);
					}
				}
			}
			// 2. Replace all \ns not preceded by \rs in the encoding by \n\rs and store the file
			for (int i = indicesOfLineBreaks.size() - 1; i >= 0; i--) {
				int currentIndex = indicesOfLineBreaks.get(i);
				content = content.substring(0, currentIndex) + "\r" + content.substring(currentIndex);
			}
//			try {
			
			ToolBox.storeTextFile(content, f);
//				Files.write(Paths.get("C:/Users/Reinier/Desktop/test_save" + extension), content.getBytes());
//				Files.write(Paths.get(encPath), enc.getBytes());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
	}


	private void importFileAction() {
		System.out.println("click import");
	}


	private void exportFileAction() {
		System.out.println("click export");
	}


	/**
	 * Converts the encoding in the encoding area into the chosen tablature style, and shows 
	 * that in the TabViewer window. In case the encoding contains errors, an error message 
	 * is given and the TabViewer is not opened. 
	 * 
	 * This is the action performed when clicking the View button in the EncodingViewer.
	 * 
	 * @param rawEnc
	 */ 
	private void viewButtonAction() {
		final int firstErrorCharIndex = 0;
		final int lastErrorCharIndex = 1;
		final int errorStringIndex = 2;
		final int ruleStringIndex = 3;
		
		String rawEnc = getEncodingTextArea().getText();

		// 1. Create an unchecked encoding
		// Every time the viewbutton is clicked, a new rawEnc is made. The first time the 
		// viewbutton is clicked, encodingArea.getText() will always be exactly as in the 
		// file that is loaded because it is set as such in openFileAction(). Any next time,
		// it will be exactly what is in the encodingArea (which now may have corrections 
		// compared to what is in the loaded file)
		Encoding enc = new Encoding(rawEnc, "", Stage.MINIMAL);
		// a. If the encoding contains metadata errors: place error message
		if (Encoding.checkMetadata(rawEnc)) {
			getUpperErrorLabel().setText(METADATA_ERROR);
			getLowerErrorLabel().setText("");
//			getErrorMessageLabel("upper").setText(Encoding.METADATA_ERROR);
//			getErrorMessageLabel("lower").setText("");
		}
		// b. If the encoding contains no metadata errors: continue
		else {
			enc = new Encoding(rawEnc, "", Stage.METADATA_CHECKED);
			String cleanEnc = enc.getCleanEncoding();
			// 2. Check the encoding
			// Remove any remaining highlights and error messages
			getHighlighter().removeAllHighlights();
			getUpperErrorLabel().setText("(none)");
			getLowerErrorLabel().setText(null);
//			getErrorMessageLabel("upper").setText("(none)");
//			getErrorMessageLabel("lower").setText(null);
			// a. If the encoding contains encoding errors: place error messages and highlight
			String[] encErrs = Encoding.checkEncoding(rawEnc, cleanEnc, enc.getTabSymbolSet());
			if (encErrs != null) {
				getUpperErrorLabel().setText(encErrs[errorStringIndex]);
				getLowerErrorLabel().setText(encErrs[ruleStringIndex]);
//				getErrorMessageLabel("upper").setText(encErrs[errorStringIndex]);
//				getErrorMessageLabel("lower").setText(encErrs[ruleStringIndex]);
				int hilitStartIndex = Integer.parseInt(encErrs[firstErrorCharIndex]);
				int hilitEndIndex = Integer.parseInt(encErrs[lastErrorCharIndex]);
				Highlighter.HighlightPainter painter = 
					new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
				try {
					getHighlighter().addHighlight(hilitStartIndex, hilitEndIndex, painter);
				} catch (BadLocationException e) {  
					System.err.println("BadLocationException: " + e.getMessage());
				}
			}
			// b. If the encoding contains no encoding errors: show the tablature in a new window 
			else {
				enc = new Encoding(rawEnc, "", Stage.SYNTAX_CHECKED);
//				List<String> types = new ArrayList<>();
//				Arrays.asList(TabSymbolSet.values()).forEach(tss -> {
//					if (!types.contains(tss.getType())) {
//						types.add(tss.getType());
//					}
//				});

				// Determine TabSymbolSet
				TabSymbolSet tss = null;
				for (AbstractButton b : Collections.list(getTabTypeButtonGroup().getElements())) {
					if (b.isSelected()) {
						tss = TabSymbolSet.getTabSymbolSet(null, b.getText());
						break;
					}
				}
//				outerLoop: for (String type : types) {
//					if (getTabRadioButton(type).isSelected()) {
//						for (TabSymbolSet t : TabSymbolSet.values()) {
//							if (t.getType().equals(type)) {
////							if (t.getName().toLowerCase().startsWith(type)) {
//								tss = t;
//								break outerLoop;
//							}
//						}
//					}
//				}

//				getTabFrameTextArea().setText(enc.visualise(tss, 
//					getRhythmSymbolsCheckBox().isSelected(), true, true));
//				initializeTabViewer(encPath);

				new ViewerOld(/*getFileName(Encoding.EXTENSION)*/getFile(), enc.visualise(tss, getRhythmSymbolsCheckBox().isSelected(), true, true), false);
			} 
		}
	}


	private String getFileName(String extension) {
		return getTitle().substring(0, getTitle().indexOf(extension));
	}


	private JScrollPane getTabViewerPane() { // alternative for getTabViewerPanel()
		return new JScrollPane(getTabTextArea(), ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}


	private JScrollPane getEncodingAreaScrollPane() {
//		if (encodingAreaScrollPane == null) {
//			encodingAreaScrollPane = 
		JScrollPane encodingAreaScrollPane = 		
			new JScrollPane(getEncodingTextArea(), ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		encodingAreaScrollPane.setBounds(new Rectangle(15, 115, 676, 430));
//		}
		return encodingAreaScrollPane;
	}


//	private void setTabPanel() {			
//	JPanel p = new JPanel();
//	p.setLayout(null);
//	p.setSize(new Dimension(PANEL_DIMS[0], PANEL_DIMS[1]));

//	// Tab area with scroll pane
//	JScrollPane tabAreaScrollPane = 
//		new JScrollPane(getTabTextArea(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
//		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//	// See encodingAreaScrollPane.setBounds() for coordinates
//	tabAreaScrollPane.setBounds(new Rectangle(15, 115-100, 676, 430+100));
//	p.add(tabAreaScrollPane, null);
//	tabPanel = p;
//}	
	
	
//	private void ssetEncodingFrameTextArea() {
//		JTextArea ta = new JTextArea();
//		ta.setBounds(new Rectangle(15, 105, 571, 76));
//		ta.setLineWrap(true); // necessary because of JScrollPane
//		ta.setEditable(true);
//		ta.setFont(FONT);
//		ta.setHighlighter(getHighlighter());
//		encodingFrameTextArea = ta;
//	}


//	private void ssetTabFrameTextArea(String text) {
//		JTextArea ta = new JTextArea();
//		ta.setBounds(new Rectangle(15, 240, 571, 136));
//		ta.setEditable(false);
//		ta.setFont(FONT);
//		ta.setText(text);
//		tabFrameTextArea = ta;
//	}
	
	
//	/**
//	 * Opens the TabViewer window.
//	 * 
//	 * @param encPath
//	 */
//	private void initializeTabViewer(String encPath) {
//		JFrame tablatureFrame = new JFrame();
//		tablatureFrame.setSize(FRAME_DIMS[0], FRAME_DIMS[1]);
//		tablatureFrame.setJMenuBar(getTabFrameMenubar(encPath));
//		tablatureFrame.setContentPane(createTabViewerPanel());
//		tablatureFrame.setTitle(".tab");
//		tablatureFrame.setVisible(true);
//		tablatureFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
//	}


//	private JRadioButton getTabRadioButton(String type) {
//	switch (type) {
//		case "French":
//			return frenchTabRadioButton;
//		case "Italian":
//			return italianTabRadioButton;
//		case "Spanish":
//			return spanishTabRadioButton;
//		case "German":
//			return germanTabRadioButton;
////		case "tabCode":
////			return tabCodeRadioButton;
//		default:
//			return null;
//	}
//}
	
	
//	private JLabel getErrorMessageLabel(String type) {
//		if (type.equals("upper")) {
//			return upperErrorLabel; 
//		}
//		else if (type.equals("lower")) {
//			return lowerErrorLabel; 
//		}
//		else { 
//			return null;
//		}
//	}


//	private void prepareTabViewer() {
//		getEncodingArea().setHighlighter(getHighlighter());
////		getEncodingArea().setHighlighter(hilit);
////		encodingArea.setHighlighter(hilit);
//		ButtonGroup tabButtons = new ButtonGroup();
//		tabButtons.add(frenchTabRadioButton);
//		tabButtons.add(italianTabRadioButton);
//		tabButtons.add(spanishTabRadioButton);
//		tabButtons.add(germanTabRadioButton);
////		tabButtons.add(tabCodeRadioButton);
//		frenchTabRadioButton.setSelected(true);
//	}


//	private JRadioButton getFrenchTabRadioButton() {
////		if (frenchTabRadioButton == null) {
//		frenchTabRadioButton = new JRadioButton("French tablature");
//		frenchTabRadioButton.setBounds(new Rectangle(99, 42, 106, 16));
////		frenchTabRadioButton.addActionListener(new ActionListener() {
////			public void actionPerformed(ActionEvent e) {
////				// TODO Auto-generated Event stub actionPerformed()
////			}
////		});
////		}
//		return frenchTabRadioButton;
//	}


//	private JRadioButton getItalianTabRadioButton() {
////		if (italianTabRadioButton == null) {
//		italianTabRadioButton = new JRadioButton("Italian tablature");
//		italianTabRadioButton.setBounds(new Rectangle(207, 42, 106, 16));
////		italianTabRadioButton.addActionListener(new ActionListener() {
////	    	public void actionPerformed(ActionEvent e) {
////	    		// TODO Auto-generated Event stub actionPerformed()
////	    	}
////	    });
////		}
//	    return italianTabRadioButton;
//	}


//	private JRadioButton getSpanishTabRadioButton() {
////		if (spanishTabRadioButton == null) {
//		spanishTabRadioButton = new JRadioButton("Spanish tablature");
//		spanishTabRadioButton.setBounds(new Rectangle(315, 42, 106, 16));
////		spanishTabRadioButton.addActionListener(new ActionListener() {
////			public void actionPerformed(ActionEvent e) {
////				// TODO Auto-generated Event stub actionPerformed()
////			}
////		});
////		}
//		return spanishTabRadioButton;
//	}


//	private JRadioButton getGermanTabRadioButton() {
////		if (germanTabRadioButton == null) {
//		germanTabRadioButton = new JRadioButton("German tablature");
//		germanTabRadioButton.setBounds(new Rectangle(423, 42, 106, 16));
////		germanTabRadioButton.addActionListener(new ActionListener() {
////			public void actionPerformed(ActionEvent e) {
////				// TODO Auto-generated Event stub actionPerformed()
////			}
////		});
////		}
//		return germanTabRadioButton;
//	}


//	private JRadioButton getTabCodeRadioButton() {
////		if (tabCodeRadioButton == null) {
//		tabCodeRadioButton = new JRadioButton("TabCode");
//		tabCodeRadioButton.setBounds(new Rectangle(531, 42, 106, 16));
////		tabCodeRadioButton.addActionListener(new ActionListener() {
////			public void actionPerformed(ActionEvent e) {
////				// TODO Auto-generated Event stub actionPerformed()
////			}
////		});
////		}
//		return tabCodeRadioButton;
//	}


//	private JCheckBox getRhythmSymbolsCheckBox() {
//		if (rhythmSymbolsCheckBox == null) {
//		rhythmSymbolsCheckBox = new JCheckBox();
//		rhythmSymbolsCheckBox.setBounds(new Rectangle(15, 559, 211, 16));
//		rhythmSymbolsCheckBox.setActionCommand("Show all rhythm symbols");
//		rhythmSymbolsCheckBox.setText("Ignore repeated rhythm symbols");
//		rhythmSymbolsCheckBox.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				// TODO Auto-generated Event stub actionPerformed()
//			}
//		});
//		}
//		return rhythmSymbolsCheckBox;
//	}


//	private void setTabFrameMenuBarOLD(String encPath) {
//		JMenuBar mb = new JMenuBar();
//	
//		// File
//		JMenu fileMenu = new JMenu("File");
//		mb.add(fileMenu);   
//	
//		// Menu item File > Save
//		JMenuItem saveMenuItem = new JMenuItem("Save");
//		saveMenuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				saveFileAction(getTabFrameTextArea().getText(), encPath);
//			}
//		});
//		fileMenu.add(saveMenuItem);
//
//		// Edit
//		fileMenu = new JMenu("Edit");
//		mb.add(fileMenu);
//		// Menu item Edit > Select all
//		JMenuItem selectAllMenuItem = new JMenuItem("Select all");
//		selectAllMenuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				getTabFrameTextArea().selectAll();
//			}
//		});
//		fileMenu.add(selectAllMenuItem);
//		tabFrameMenuBar = mb;
//	}


//	private void setEncodingFrameMenuBarOLD(String encPath) {
//		JMenuBar mb = new JMenuBar();
//
//		// File menu
//		JMenu fileMenu = new JMenu("File");
//		mb.add(fileMenu);
//	
//		// Menu item File > Open
//		JMenuItem openMenuItem = new JMenuItem("Open");
//		openMenuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				openFileAction(encPath);
//			}
//		});
//		fileMenu.add(openMenuItem);
//	
//		// Menu item File > Save
//		JMenuItem saveMenuItem = new JMenuItem("Save");
//		saveMenuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				saveFileAction(getEncodingFrameTextArea().getText(), encPath);
//			}
//		});
//		fileMenu.add(saveMenuItem);
//		encodingFrameMenuBar = mb;
//	}


//	private JButton getViewButton(String encPath) {
//		JButton viewButton = new JButton();
//		viewButton.setBounds(new Rectangle(600, 559, 91, 31));
//		viewButton.setText("View");
//		viewButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				viewButtonAction(getEncodingTextArea().getText(), encPath);
////				viewButtonAction(encPath);
//			}
//		});
//		return viewButton;
//	}
}
