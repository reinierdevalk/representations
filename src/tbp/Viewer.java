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
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import path.Path;
import tbp.TabSymbol.TabSymbolSet;
import tools.ToolBox;

public class Viewer extends JFrame{

	private static final long serialVersionUID = 1L;

	private static final Integer[] FRAME_DIMS = new Integer[]{1360, 680}; // 1345 + 15
	private static final Integer[] PANEL_DIMS = new Integer[]{1345, 413}; // 15 + 650 (scroll pane width) + 15 + 650 + 15
	private static final Font FONT = new Font("Courier New", Font.PLAIN, 12);
	private static final String TOOL_NAME = "tab+Editor";
	private static final String ASCII_EXTENSION = ".tab";
	private static final String TABCODE_EXTENSION = ".tc";
	private static final String MEI_EXTENSION = ".xml";
	private static final String[] TITLE = new String[]{"untitled", Encoding.EXTENSION, " - " + TOOL_NAME};

	private Highlighter highlighter;
//	private JLabel pieceLabel;
	private ButtonGroup tabTypeButtonGroup;
	private JLabel upperErrorLabel;
	private JLabel lowerErrorLabel;
	private JTextArea encodingTextArea;
	private JTextArea tabTextArea;
	private JCheckBox rhythmSymbolsCheckBox;
	private JButton viewButton;	
	private JMenuBar editorMenuBar;
	private JPanel editorPanel;
	private JFileChooser editorFileChooser;
	private File editorFile;


	public static void main(String[] args) {
		new Viewer();
	}


	///////////////////////////////
	//
	//  C O N S T R U C T O R S
	//
	public Viewer() {
		super();
		init();
	}


	private void init() {
		setHighlighter();
//		setPieceLabel();
		setTabTypeButtonGroup();
		setUpperErrorLabel();
		setLowerErrorLabel();
		setEncodingTextArea();
		setTabTextArea("");
		setRhythmSymbolsCheckBox();
		setViewButton();
		setEditorMenuBar();
		setEditorPanel();
		//
		setJMenuBar(getEditorMenuBar());
		setContentPane(getEditorPanel());
		setEditorFileChooser();
		setEditorFile(null);
		setSize(FRAME_DIMS[0], FRAME_DIMS[1]);		
		setVisible(true);
		setTitle(TITLE[0] + TITLE[1] + TITLE[2]);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}


	//////////////////////////////
	//
	//  S E T T E R S  
	//  for instance variables
	//
	private void setHighlighter() {
		highlighter = new DefaultHighlighter();
	}


//	private void setPieceLabel() {
//		JLabel l = new JLabel();
//		l.setBounds(99, 17, 592, 14);
//		pieceLabel = l;
//	}


	private void setTabTypeButtonGroup() {
		ButtonGroup bg = new ButtonGroup();
		JRadioButton rb = new JRadioButton(TabSymbolSet.FRENCH.getType());
		rb.setBounds(new Rectangle(99, 559, 106, 16));
//		rb.setBounds(new Rectangle(99, 15, 106, 16));
//		rb.setBounds(new Rectangle(99, 42, 106, 16));
		rb.setSelected(true);
		bg.add(rb);
		rb = new JRadioButton(TabSymbolSet.ITALIAN.getType());
		rb.setBounds(new Rectangle(207, 559, 106, 16));
//		rb.setBounds(new Rectangle(207, 15, 106, 16));
//		rb.setBounds(new Rectangle(207, 42, 106, 16));
		bg.add(rb);
		rb = new JRadioButton(TabSymbolSet.SPANISH.getType());
		rb.setBounds(new Rectangle(315, 559, 106, 16));
//		rb.setBounds(new Rectangle(315, 15, 106, 16));
//		rb.setBounds(new Rectangle(315, 42, 106, 16));
		bg.add(rb);
		rb = new JRadioButton(TabSymbolSet.NEWSIDLER_1536.getType());
		rb.setBounds(new Rectangle(423, 559, 106, 16));
//		rb.setBounds(new Rectangle(423, 15, 106, 16));
//		rb.setBounds(new Rectangle(423, 42, 106, 16));
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


	private JTextArea makeTextArea(boolean encoding, String content) {
		JTextArea ta = new JTextArea();
		// TODO these bounds are overridden by those of the scrollpanes in the JPanels
		ta.setBounds(encoding ? new Rectangle(15, 105, 571, 76) : 
			new Rectangle(15 + 571 + 15, 105, 571, 76));
//		ta.setBounds(encodingFrame ? new Rectangle(15, 105, 571, 76) : new Rectangle(15, 240, 571, 136));
		ta.setLineWrap(true); // necessary because of JScrollPane
		ta.setEditable(true);
		ta.setFont(FONT);
		ta.setText(content);
		ta.setHighlighter(encoding ? getHighlighter() : null); // highligher also makes it copyable
		return ta;
	}


	private void setTabTextArea(String content) {
		tabTextArea = makeTextArea(false, content);
	}


	private void setRhythmSymbolsCheckBox() {
		JCheckBox cb = new JCheckBox();
		cb.setBounds(new Rectangle(15, 586, 261, 16));
//		cb.setBounds(new Rectangle(15, 559, 261, 16));
//		cb.setBounds(new Rectangle(15, 42, 261, 16));
//		cb.setBounds(new Rectangle(15, 559, 261, 16));
//		cb.setActionCommand("Show all rhythm symbols");
		cb.setText("Do not show repeated rhythm symbols");
		rhythmSymbolsCheckBox = cb;
	}


	private void setViewButton() {
		JButton b = new JButton();
		b.setBounds(new Rectangle(574, 559, 91, 31));
//		b.setBounds(new Rectangle(600, 559, 91, 31));
		b.setText("View");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewButtonAction();
			}
		});
		viewButton = b;
	}


	private void setEditorMenuBar() {
		editorMenuBar = makeEditorMenuBar();
	}


	private JMenuBar makeEditorMenuBar() {
		JMenuBar mb = new JMenuBar();
//		JTextArea ta = encodingFrame ? getEncodingTextArea() : getTabTextArea();

		JMenu m = new JMenu("File");
		mb.add(m);

		JMenuItem newMenuItem = new JMenuItem("New");
		newMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newFileAction();
			}
		});
		m.add(newMenuItem);
		JMenuItem openMenuItem = new JMenuItem("Open");
		openMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFileAction();
			}
		});
		m.add(openMenuItem);
		JMenuItem saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFileAction(Encoding.EXTENSION);
			}
		});
		m.add(saveMenuItem);
		JMenuItem saveAsMenuItem = new JMenuItem("Save as");
		saveAsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAsFileAction(Encoding.EXTENSION);
			}
		});
		m.add(saveAsMenuItem);
		JMenu sm = new JMenu("Import");
		JMenuItem asciiImportSubmenuItem = new JMenuItem("ASCII tab");
		asciiImportSubmenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importFileAction(ASCII_EXTENSION);
			}
		});
		sm.add(asciiImportSubmenuItem);
		JMenuItem tabCodeSubmenuItem = new JMenuItem("TabCode");
		tabCodeSubmenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importFileAction(TABCODE_EXTENSION);
			}
		});
		sm.add(tabCodeSubmenuItem);
		m.add(sm);
		sm = new JMenu("Export");
		JMenuItem asciiSubmenuItem = new JMenuItem("ASCII tab");
		asciiSubmenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportFileAction(ASCII_EXTENSION);
			}
		});
		sm.add(asciiSubmenuItem);
		JMenuItem tabMEISubmenuItem = new JMenuItem("MEI");
		tabMEISubmenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportFileAction(MEI_EXTENSION);
			}
		});
		sm.add(tabMEISubmenuItem);
		m.add(sm);
		
		m = new JMenu("Edit");
		mb.add(m);
		JMenuItem selectAllMenuItem = new JMenuItem("Select all");
		selectAllMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getEncodingTextArea().requestFocus();
				getEncodingTextArea().selectAll();
			}
		});
		m.add(selectAllMenuItem);

		return mb;
	}


	private void setEditorPanel() {
		editorPanel = makeEditorPanel();
	}


	private JPanel makeEditorPanel() {
		JPanel p = new JPanel();
		p.setLayout(null);
		p.setSize(new Dimension(PANEL_DIMS[0], PANEL_DIMS[1]));

//		JLabel l = new JLabel("Piece:");
//		l.setBounds(new Rectangle(15, 15, 81, 16));
//		p.add(l, null);
//		p.add(getPieceLabel());
		//
		JLabel l = new JLabel("View as:");
		l.setBounds(new Rectangle(15, 559, 81, 16));
//		l.setBounds(new Rectangle(15, 15, 81, 16));
//		l.setBounds(new Rectangle(15, 42, 81, 16));
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

		JScrollPane sp = 
			new JScrollPane(getEncodingTextArea(), 
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setBounds(new Rectangle(15, 115, 650, 430));
		p.add(sp, null);
		sp = 
			new JScrollPane(getTabTextArea(), 
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setBounds(new Rectangle(15 + 15 + 650, 115, 650, 430));
		p.add(sp, null);

		return p;
	}


	private void setEditorFileChooser() {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(Path.ROOT_PATH_USER + Path.ENCODINGS_PATH));
		editorFileChooser = fc;
	}


	private void setEditorFile(File f) {
		editorFile = f;
	}


//	private void setTabPanel() {
//		tabPanel = makePanel(false);
//	}


//	private void setTabMenuBar() {
//		tabMenuBar = makeMenuBar(false);
//	}


	//////////////////////////////
	//
	//  G E T T E R S
	//  for instance variables
	//
	private Highlighter getHighlighter() {
		return highlighter;
	}


//	private JLabel getPieceLabel() {
//		return pieceLabel;
//	}


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


	private JTextArea getTabTextArea() {
		return tabTextArea;
	}


	private JCheckBox getRhythmSymbolsCheckBox() {
		return rhythmSymbolsCheckBox; 
	}


	private JButton getViewButton() {
		return viewButton;
	}


	private JMenuBar getEditorMenuBar() {
		return editorMenuBar;
	}


	private JPanel getEditorPanel() {
		return editorPanel;
	}


	private JFileChooser getEditorFileChooser() {
		return editorFileChooser;
	}


	private File getEditorFile() {
		return editorFile;
	}


	//////////////////////////////////////
	//
	//  I N S T A N C E  M E T H O D S
	//
	private void newFileAction() {
		setTitle(TITLE[0] + TITLE[1] + TITLE[2]);
		setEditorFile(null);
		String s = "";
		for (String t : Encoding.METADATA_TAGS) {
			s += Encoding.OPEN_METADATA_BRACKET + t + ":" + Encoding.CLOSE_METADATA_BRACKET + "\r\n";
		}
		s += Symbol.END_BREAK_INDICATOR;
		setTextAreaContent(s, true);
//		getEncodingTextArea().setText(s);
//		getEncodingTextArea().setCaretPosition(0);
		setTextAreaContent("", false);
//		getTabTextArea().setText("");
//		getTabTextArea().setCaretPosition(0);
	}


	private void openFileAction() {
		setTextAreaContent("", false);
		getEditorFileChooser().setDialogType(JFileChooser.OPEN_DIALOG);
		getEditorFileChooser().setDialogTitle("Open");
		// Set file type filter
		getEditorFileChooser().setFileFilter(new FileNameExtensionFilter("tab+ (.tbp)", 
			Encoding.EXTENSION.substring(1)));
		// Remove any previous selection
		getEditorFileChooser().setSelectedFile(new File(""));
		if (getEditorFileChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = null;
			try {
				f = getEditorFileChooser().getSelectedFile();
				BufferedReader br = new BufferedReader(new FileReader(getEditorFileChooser().getSelectedFile()));						
			} catch (IOException e) {
				// 11:11
				// https://www.youtube.com/watch?v=Z8p_BtqPk78
				e.printStackTrace();
			}
			setEditorFile(f);
			setTitle(f.getName() + " - " + TOOL_NAME);
//			fileName = f.getName();
//			String rawEnc = ToolBox.readTextFile(encFile);
			String rawEncoding = "";
			try {
				rawEncoding = ToolBox.readTextFile(f);
				rawEncoding = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			setTextAreaContent(rawEncoding, true);
//			getEncodingTextArea().setText(rawEncoding);
//			getEncodingTextArea().setCaretPosition(0);
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
		
//			getPieceLabel().setText(encFile.getName());
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
		if (getEditorFile() == null) {
			saveAsFileAction(extension);
		}
		// Existing file
		else {
			ToolBox.storeTextFile(content, getEditorFile());
		}
	}


	private void saveAsFileAction(String extension) {
		String content = 
			extension.equals(Encoding.EXTENSION) ? getEncodingTextArea().getText() : 
			getTabTextArea().getText();

		getEditorFileChooser().setDialogType(JFileChooser.SAVE_DIALOG);
		getEditorFileChooser().setDialogTitle("Save as");
		// Set file type filter and suggested file name 
		if (extension.equals(Encoding.EXTENSION)) {
			getEditorFileChooser().setFileFilter(new FileNameExtensionFilter("tab+ (.tbp)", extension.substring(1)));
			getEditorFileChooser().setSelectedFile(getEditorFile() == null ? new File("untitled" + extension): getEditorFile());
		}
		else {
			getEditorFileChooser().setFileFilter(new FileNameExtensionFilter("ASCII (" + ASCII_EXTENSION +")", extension.substring(1)));			
			getEditorFileChooser().setSelectedFile(new File(getEditorFile().getAbsolutePath().replace(Encoding.EXTENSION, extension)));
		}
		// https://stackoverflow.com/questions/17010647/set-default-saving-extension-with-jfilechooser
		if (getEditorFileChooser().showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = null;
//			String extension = encodingFrame ? Encoding.EXTENSION.substring(1) : ".tab";
//			try {
			
//				getFileChooser().setDialogType(JFileChooser.SAVE_DIALOG);
//				getFileChooser().setFileFilter(new FileNameExtensionFilter(null, 
//					Encoding.EXTENSION.substring(1)));
				
				
				f = getEditorFileChooser().getSelectedFile();;
//				BufferedReader br = new BufferedReader(new FileReader(getFileChooser().getSelectedFile()));
//				BufferedReader br = new BufferedReader(new FileReader(new File("")));

//			} catch (IOException e) {
//				e.printStackTrace();
//			}
				
			if (getEditorFile() == null) {
				setEditorFile(f);
				setTitle(f.getName());
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


	private void importFileAction(String extension) {
		setTextAreaContent("", false);
		getEditorFileChooser().setDialogType(JFileChooser.OPEN_DIALOG);
		getEditorFileChooser().setDialogTitle("Import");
		// Set file type filter
		if (extension.equals(TABCODE_EXTENSION)) {
			getEditorFileChooser().setFileFilter(new FileNameExtensionFilter("TabCode (" + TABCODE_EXTENSION + ")", extension.substring(1)));
		}
		else if (extension.equals(ASCII_EXTENSION)) {
			getEditorFileChooser().setFileFilter(new FileNameExtensionFilter("ASCII (" + ASCII_EXTENSION + ")", extension.substring(1)));
		}
		// Remove any previous selection
		getEditorFileChooser().setSelectedFile(new File(""));
		if (getEditorFileChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = null;
			try {
				f = getEditorFileChooser().getSelectedFile();
				BufferedReader br = new BufferedReader(new FileReader(getEditorFileChooser().getSelectedFile()));						
			} catch (IOException e) {
				// 11:11
				// https://www.youtube.com/watch?v=Z8p_BtqPk78
				e.printStackTrace();
			}
		}
	}


	private void exportFileAction(String extension) {
		getEditorFileChooser().setDialogType(JFileChooser.SAVE_DIALOG);
		getEditorFileChooser().setDialogTitle("Export");
		// Set file type filter
		if (extension.equals(".mei") || extension.equals(MEI_EXTENSION)) {
			getEditorFileChooser().setFileFilter(new FileNameExtensionFilter("MEI (.mei, " + MEI_EXTENSION + ")", extension.substring(1)));
		}
		else if (extension.equals(ASCII_EXTENSION)) {
			getEditorFileChooser().setFileFilter(new FileNameExtensionFilter("ASCII (" + ASCII_EXTENSION + ")", extension.substring(1)));
		}
		getEditorFileChooser().setSelectedFile(new File(getEditorFile().getAbsolutePath().replace(Encoding.EXTENSION, extension)));
		if (getEditorFileChooser().showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			System.out.println("Clicked export");
		}
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
		Encoding enc = new Encoding(rawEnc, "", Encoding.MINIMAL);
		// a. If the encoding contains metadata errors: place error message
		if (Encoding.checkForMetadataErrors(rawEnc)) {
			getUpperErrorLabel().setText(Encoding.METADATA_ERROR);
			getLowerErrorLabel().setText("");
//			getErrorMessageLabel("upper").setText(Encoding.METADATA_ERROR);
//			getErrorMessageLabel("lower").setText("");
		}
		// b. If the encoding contains no metadata errors: continue
		else {
			enc = new Encoding(rawEnc, "", Encoding.METADATA_CHECKED);
			String cleanEnc = enc.getCleanEncoding();
			// 2. Check the encoding
			// Remove any remaining highlights and error messages
			getHighlighter().removeAllHighlights();
			getUpperErrorLabel().setText("(none)");
			getLowerErrorLabel().setText(null);
//			getErrorMessageLabel("upper").setText("(none)");
//			getErrorMessageLabel("lower").setText(null);
			// a. If the encoding contains encoding errors: place error messages and highlight
			String[] encErrs = Encoding.checkForEncodingErrors(rawEnc, cleanEnc, enc.getTabSymbolSet());
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
				// https://stackoverflow.com/questions/8852560/how-to-make-popup-window-in-java
				JOptionPane optionPane = new JOptionPane("ErrorMsg", JOptionPane.ERROR_MESSAGE);    
				JDialog dialog = optionPane.createDialog("Failure");
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);
			}
			// b. If the encoding contains no encoding errors: show the tablature in a new window 
			else {
				enc = new Encoding(rawEnc, "", Encoding.SYNTAX_CHECKED);
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
				
				setTextAreaContent(enc.visualise(tss, getRhythmSymbolsCheckBox().isSelected(), true, true), false);
//				new Viewer(/*getFileName(Encoding.EXTENSION)*/getFile(), enc.visualise(tss, getRhythmSymbolsCheckBox().isSelected(), true, true), false);
			} 
		}
	}


	private void setTextAreaContent(String content, boolean encoding) {
		JTextArea ta = encoding ? getEncodingTextArea() : getTabTextArea(); 
		ta.setText(content);
		ta.setCaretPosition(0);
	}

}