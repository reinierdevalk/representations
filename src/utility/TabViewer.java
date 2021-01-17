package utility;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import representations.Encoding;
import tbp.ConstantMusicalSymbol;
import tbp.MensurationSign;
import tbp.RhythmSymbol;
import tbp.Staff;
import tbp.SymbolDictionary;
import tbp.TabSymbol;
import tbp.TabSymbolSet;

public class TabViewer extends JFrame{

	private static final long serialVersionUID = 1L;
	private Highlighter hilit;
	private JTextArea encodingArea;
	private JTextArea tabArea;
	private JLabel pieceInfoLabel;
	private JLabel upperErrorMessageLabel;
	private JLabel lowerErrorMessageLabel;
	private JRadioButton frenchTabRadioButton;
	private JRadioButton italianTabRadioButton;
	private JRadioButton spanishTabRadioButton;
	private JRadioButton germanTabRadioButton;
	private JRadioButton tabCodeRadioButton;
	private JCheckBox rhythmSymbolsCheckBox;
	private static List<String> tabTypes = 
		Arrays.asList(new String[]{"french", "italian", "spanish", "german", "tabCode"});
	private final Integer[] FRAME_DIMS = new Integer[]{717, 672};
	private final Integer[] PANEL_DIMS = new Integer[]{586, 413};


	public static void main(String[] args) {
		String encPath = null;
		if (args.length != 0) {
			encPath = args[0];
		}
//		TabViewer gui = 
		new TabViewer(encPath);
	}


	public TabViewer(String encPath) {
		super();
		setHilit();
		setEncodingArea();
		setTabArea();
		setPieceInfoLabel();
		setErrorMessageLabels();
		setTabRadioButtons();
		setRhythmSymbolsCheckBox();
		initializeEncodingViewer(encPath);
	}


	private List<String> getTabTypes() {
		return tabTypes;
	}


	private void setHilit() {
		hilit = new DefaultHighlighter();
	}


	private Highlighter getHilit() {
		return hilit;
	}


	private void setEncodingArea() {
		JTextArea encArea = new JTextArea();
		encArea.setLineWrap(true); // necessary because of scroll bar
		encArea.setBounds(new Rectangle(15, 105, 571, 76));
		encArea.setEditable(true);
		encArea.setFont(new Font("Courier New", Font.PLAIN, 12));
		encArea.setHighlighter(getHilit());
		encodingArea = encArea;
	}


	private JTextArea getEncodingArea() {
		return encodingArea; 
	}


	private void setTabArea() {
		JTextArea ta = new JTextArea();
		ta.setBounds(new Rectangle(15, 240, 571, 136));
		ta.setEditable(false);
		ta.setFont(new Font("Courier New", Font.PLAIN, 12));
		tabArea = ta;
	}


	private JTextArea getTabArea() {
		return tabArea;
	}


	private void setPieceInfoLabel() {
		JLabel pil = new JLabel();
		pil.setBounds(99, 17, 592, 14);
		pieceInfoLabel = pil;
	}


	private JLabel getPieceInfoLabel() {
		return pieceInfoLabel;
	}


	private void setErrorMessageLabels() {
		JLabel ueml = new JLabel();
		ueml.setBounds(new Rectangle(99, 69, 592, 16));
		ueml.setForeground(Color.RED);
		upperErrorMessageLabel = ueml;
		JLabel leml = new JLabel();
		leml.setBounds(new Rectangle(99, 88, 592, 16));
		leml.setForeground(Color.RED);
		lowerErrorMessageLabel = leml;
	}


	private JLabel getErrorMessageLabel(String type) {
		if (type.equals("upper")) {
			return upperErrorMessageLabel; 
		}
		else if (type.equals("lower")) {
			return lowerErrorMessageLabel; 
		}
		else { 
			return null;
		}
	}


	private void setTabRadioButtons() {
		JRadioButton ftrb = new JRadioButton("French tablature");
		ftrb.setBounds(new Rectangle(99, 42, 106, 16));
		frenchTabRadioButton = ftrb;
		JRadioButton itrb = new JRadioButton("Italian tablature");
		itrb.setBounds(new Rectangle(207, 42, 106, 16));
		italianTabRadioButton = itrb;
		JRadioButton strb = new JRadioButton("Spanish tablature");
		strb.setBounds(new Rectangle(315, 42, 106, 16));
		spanishTabRadioButton = strb;
		JRadioButton gtrb = new JRadioButton("German tablature");
		gtrb.setBounds(new Rectangle(423, 42, 106, 16));
		germanTabRadioButton = gtrb;
		JRadioButton tctrb = new JRadioButton("TabCode");
		tctrb.setBounds(new Rectangle(531, 42, 106, 16));
		tabCodeRadioButton = tctrb;
	}


	private JRadioButton getTabRadioButton(String type) {
		switch (type) {
			case "french":
				return frenchTabRadioButton;
			case "italian":
				return italianTabRadioButton;
			case "spanish":
				return spanishTabRadioButton;
			case "german":
				return germanTabRadioButton;
			case "tabCode":
				return tabCodeRadioButton;
			default:
				return null;
		}
	}


	private void setRhythmSymbolsCheckBox() {
		JCheckBox rscb = new JCheckBox();
		rscb.setBounds(new Rectangle(15, 559, 211, 16));
		rscb.setActionCommand("Show all rhythm symbols");
		rscb.setText("Ignore repeated rhythm symbols");
		rhythmSymbolsCheckBox = rscb;
	}


	private JCheckBox getRhythmSymbolsCheckBox() {
		return rhythmSymbolsCheckBox; 
	}


	private void initializeEncodingViewer(String encPath) {
		this.setSize(FRAME_DIMS[0], FRAME_DIMS[1]);
		this.setJMenuBar(getEncodingViewerMenubar(encPath));
		this.setContentPane(getEncodingViewerPanel(encPath));
		this.setTitle("EncodingViewer");
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}


	/**
	 * Creates the JMenuBar for the EncodingViewer.
	 * 
	 * @param encPath
	 * @return
	 */
	private JMenuBar getEncodingViewerMenubar(String encPath) {
		JMenuBar encodingViewerMenubar = new JMenuBar();
		
		// File
		JMenu fileMenu = new JMenu("File");
		encodingViewerMenubar.add(fileMenu);
		// File > Open
		JMenuItem openFile = new JMenuItem("Open"); 
		fileMenu.add(openFile); 
		openFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFileAction(encPath);
			}
		});
		// File > Save
		JMenuItem saveFile = new JMenuItem("Save");
		fileMenu.add(saveFile);
		saveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFileAction(getEncodingArea().getText(), encPath);
			}
		});
		return encodingViewerMenubar;
	}


	/**
	 * Creates the JPanel for the EncodingViewer and all its content.
	 * 
	 * @param encPath
	 * @return
	 */
	private JPanel getEncodingViewerPanel(String encPath) {
		JPanel encodingViewerPanel = new JPanel();
		encodingViewerPanel.setLayout(null);
		encodingViewerPanel.setSize(new Dimension(PANEL_DIMS[0], PANEL_DIMS[1]));

		// Encoding labels
		JLabel pieceLabel = new JLabel("Encoding:");
		pieceLabel.setBounds(new Rectangle(15, 15, 81, 16));
		encodingViewerPanel.add(pieceLabel, null);
		encodingViewerPanel.add(getPieceInfoLabel());

		// View-as label and tablature radio buttons
		JLabel viewAsLabel = new JLabel("View as:");
		viewAsLabel.setBounds(new Rectangle(15, 42, 81, 16));
		encodingViewerPanel.add(viewAsLabel, null);
		List<String> types = getTabTypes();
		for (String type : types) {
			encodingViewerPanel.add(getTabRadioButton(type), null);
		}
		ButtonGroup tabButtons = new ButtonGroup();
		for (String type : types) {
			JRadioButton curr = getTabRadioButton(type);
			tabButtons.add(curr);
			if (type.equals("french")) {
				curr.setSelected(true);
			}
		}

		// Error labels
		JLabel errorLabel = new JLabel("Error:");
		errorLabel.setBounds(15, 69, 81, 16);
		encodingViewerPanel.add(errorLabel, null);
		encodingViewerPanel.add(getErrorMessageLabel("upper"), null);
		encodingViewerPanel.add(getErrorMessageLabel("lower"), null);

		// Encoding area with scroll pane
		JScrollPane encodingAreaScrollPane =
			new JScrollPane(getEncodingArea(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,	
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		encodingAreaScrollPane.setBounds(new Rectangle(15, 115, 676, 430));
		encodingViewerPanel.add(encodingAreaScrollPane, null);

		// Repeated RS checkbox
		encodingViewerPanel.add(getRhythmSymbolsCheckBox(), null);

		// View button
		JButton viewButton = new JButton();
		viewButton.setBounds(new Rectangle(600, 559, 91, 31));
		viewButton.setText("View");
		viewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewButtonAction(getEncodingArea().getText(), encPath);
			}
		});
		encodingViewerPanel.add(viewButton, null);

		return encodingViewerPanel;
	}


	/**
	 * Loads the content of a file into <code>encodingArea</code>. 
	 * 
	 * This is the action performed when clicking File > Open from the EncodingViewer menu.
	 * 
	 * @param encPath The path from which to open the file.
	 */
	private void openFileAction(String encPath) {
		// TODO encodingFile is now hardcoded; make possible to select file from menu
		if (encPath == null) {
			String prefix = "F:/research/data/data/encodings/";
			encPath = prefix;
			
			// Test
//			encPath += "test/testpiece.tbp";
//			encPath += "test/test_get_meter_info.tbp";
//			encPath =  "F:/research/publications/conferences-workshops/2019-ISMIR/paper/test/tab/3610_033_inter_natos_mulierum_morales_T-rev.tbp";
//			encPath =  "F:/research/publications/conferences-workshops/2019-ISMIR/paper/test/tab/3618_041_benedictus_from_missa_de_l_homme_arme_morales_T.tbp";
//			encPath =  "F:/research/projects/byrd/test/il_me_souffit-short.tbp";
//			encPath =  "F:/research/projects/byrd/test/pleni.tbp";
			
			// Need to be double-checked
//			encPath += "newsidler-1536_6-mein_einigs_a.tbp";
//			encPath += "Newsidler 1536 - Mein hertz alzeyt hat gross verlangen.tbp";
//			encPath += "Ochsenkun 1558 - Cum Sancto spiritu.tbp";  	
//			encPath += "Barbetta 1582 - Martin menoit.tbp";
//			encPath += "Da Crema 1546 - Il nest plaisir.tbp";
//			encPath += "De Narbaez 1538 - MIlle regres.tbp";
//			encPath += "Heckel 1562 - Il est vne Fillete. [Tenor].tbp";
//			encPath += "Heckel 1562 - Il estoit vne fillete. Discant.tbp";
//			encPath += "Morlaye 1552 - LAs on peut iuger.tbp";
//			encPath += "Newsidler 1544 - Der hupff auf.tbp";
//			encPath += "Newsidler 1544 - Hie volget die Schlacht vor Bafia. Der Erst Teyl.tbp";
//			encPath += "Newsidler 1544 - Hie volget die Schlacht vor Bafia. Der ander Teyl der schlacht.tbp";
//			encPath += "Newsidler 1544 - Sula Bataglia.tbp";
//			encPath += "Ochsenkun 1558 - Benedicta es coelorum, Prima pars.tbp";
//			encPath += "Ochsenkun 1558 - Gott alls in allem wesentlich.tbp";
//			encPath += "Ochsenkun 1558 - Pater Noster, Prima pars.tbp"; 
//			encPath += "Ochsenkun 1558 - Praeter rerum seriem, Prima pars.tbp";
//			encPath += "Ochsenkun 1558 - Stabat mater dolorosa, Prima pars.tbp";
//			encPath += "Phalese 1546 - Martin menuyt de Iennequin.tbp";
//			encPath += "Spinacino 1507 - LA Bernardina de Iosquin.tbp";
			
			// Checked and ready for processing
			// 3vv
			encPath += "tab-int/3vv/" + "newsidler-1536_7-disant_adiu.tbp";
//			encPath += "tab-int/3vv/" + "newsidler-1536_7-mess_pensees.tbp";
//			encPath += "tab-int/3vv/" + "pisador-1552_7-pleni_de.tbp";
//			encPath += "tab-int/3vv/" + "judenkuenig-1523_2-elslein_liebes.tbp";
//			encPath += "tab-int/3vv/" + "newsidler-1544_2-nun_volget.tbp";
//			encPath += "tab-int/3vv/" + "phalese-1547_7-tant_que-a3.tbp"; 

			// 4vv
//			encPath += "tab-int/4vv/" + "ochsenkun_1558_-_absolon_fili.tbp";
//			encPath += "tab-int/4vv/" + "ochsenkun_1558_-_in_exitu.tbp";
//			encPath += "tab-int/4vv/" + "ochsenkun_1558_-_qui_habitat.tbp";
//			encPath += "tab-int/4vv/" + "rotta-1546_15-bramo_morir.tbp";
//			encPath += "tab-int/4vv/" + "phalese_1547_-_tant_que_a4.tbp";
//			encPath += "tab-int/4vv/" + "ochsenkun_1558_-_herr_gott.tbp";
//			encPath += "tab-int/4vv/" + "abondante-1548_1-mais_mamignone.tbp";
//			encPath += "tab-int/4vv/" + "phalese_1563_-_las_on.tbp";
//			encPath += "tab-int/4vv/" + "barbetta_1582_-_il_nest.tbp";
//			encPath += "tab-int/4vv/" + "phalese_1563_-_il_estoit.tbp";
//			encPath += "tab-int/4vv/" + "BSB-mus.ms._272-mille_regres.tbp";

			// 5vv
//			encPath += "tab-int/5vv/" + "adriansen_1584_-_dvn_si.tbp";
//			encPath += "tab-int/5vv/" + "ochsenkun_1558_-_inuiolata_integra.tbp";
			
			// Byrd
//			encPath += "byrd-int/4vv/as_caesar_wept-II.tbp";

			// JosquIntab
//			encPath = "F:/research/data/data/josquintab/tab/" + "5256_05_inviolata_integra_desprez-2.tbp";
//			encPath = "F:/research/data/data/josquintab/tab/" + "5263_12_in_exitu_israel_de_egipto_desprez-3.tbp";
		}
		File encFile = new File(encPath);
		
		getPieceInfoLabel().setText(encFile.getName());
		String rawEncoding = "";
		try {
			rawEncoding = new String(Files.readAllBytes(Paths.get(encFile.getAbsolutePath())));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		getEncodingArea().setText(rawEncoding);
	}


	/**
	 * Saves the encoding in the <code>encodingArea</code> in a file.
	 * 
	 * This is the action performed when clicking File > Save from the EncodingViewer and 
	 * TabViewer menu.
	 * 
	 * @param enc The encoding
	 * @param encPath The path to save the encoding to. 
	 * 
	 */
	private void saveFileAction(String enc, String encPath) {
		// Handle any returns added to encoding (which will be "\n" and not "\r\n") by 
		// replacing them with "\r\n"
		// 1. List all indices of the \ns not preceded by \rs
		List<Integer> indicesOfLineBreaks = new ArrayList<Integer>(); 
		for (int i = 0; i < enc.length(); i++) {
			String currentChar = enc.substring(i, i + 1);
			if (currentChar.equals("\n")) {
				// NB: previousChar always exists as the char at index 0 in encoding will
				// never be a \n
				String previousChar = enc.substring(i - 1, i);	
				if (!previousChar.equals("\r")) {
					indicesOfLineBreaks.add(i);
				}
			}
		}  	
		// 2. Replace all \ns not preceded by \rs in the encoding by \n\rs and store the file
		for (int i = indicesOfLineBreaks.size() - 1; i >= 0; i--) {
			int currentIndex = indicesOfLineBreaks.get(i);
			enc = enc.substring(0, currentIndex) + "\r" + enc.substring(currentIndex);
		}
		try {
			Files.write(Paths.get(encPath), enc.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
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
	 * @param encPath
	 */ 
	private void viewButtonAction(String rawEnc, String encPath) { 
		final int firstErrorCharIndex = 0;
		final int lastErrorCharIndex = 1;
		final int errorStringIndex = 2;
		final int ruleStringIndex = 3;
		
		// 1. Create an unchecked encoding
		// The first time the viewbutton is clicked, encodingArea.getText() will always be 
		// exactly as in the file that is loaded because it is set as such in openFileAction().
		// Any next time, it will be exactly what is in the encodingArea (which now may have 
		// corrections compared to what is in the loaded file)
		Encoding enc = new Encoding(rawEnc, false);
		// a. If the encoding contains metadata errors: place error message
		if (enc.checkForMetadataErrors()) {
			getErrorMessageLabel("upper").setText(Encoding.METADATA_ERROR);
			getErrorMessageLabel("lower").setText("");
		}
		// b. If the encoding contains no metadata errors: continue
		else {
			// 2. Check the encoding
			// Remove any remaining highlights and error messages
			getHilit().removeAllHighlights();
			getErrorMessageLabel("upper").setText("(none)");
			getErrorMessageLabel("lower").setText(null);
			// a. If the encoding contains encoding errors: place error messages and highlight
			if (enc.checkForEncodingErrors() != null) { // needs rawEncoding, cleanEncoding, and infoAndSettings
				getErrorMessageLabel("upper").setText(enc.checkForEncodingErrors()[errorStringIndex]);
				getErrorMessageLabel("lower").setText(enc.checkForEncodingErrors()[ruleStringIndex]);
				int hilitStartIndex = Integer.parseInt(enc.checkForEncodingErrors()[firstErrorCharIndex]);
				int hilitEndIndex = Integer.parseInt(enc.checkForEncodingErrors()[lastErrorCharIndex]);
				Highlighter.HighlightPainter painter = 
					new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
				try {
					getHilit().addHighlight(hilitStartIndex, hilitEndIndex, painter);
				} catch (BadLocationException e) {  
					System.err.println("BadLocationException: " + e.getMessage());
				}
			}
			// b. If the encoding contains no encoding errors: show the tablature in a new window 
			else {
				StringBuffer metaData = new StringBuffer();
				enc.getMetaData().forEach(s -> metaData.append(s + "\n"));
				StringBuffer footnotes = new StringBuffer();
				enc.getFootnotes().forEach(s -> footnotes.append(s + "\n"));
				// Determine TabSymbolSet
				TabSymbolSet tss = null;
				outerLoop: for (String type : getTabTypes()) {
					if (getTabRadioButton(type).isSelected()) {   
						for (TabSymbolSet t : TabSymbolSet.getTabSymbolSets()) {
							if (t.getName().toLowerCase().startsWith(type)) {
								tss = t;
								break outerLoop;
							}
						}
					}
				}
				getTabArea().setText(
					metaData.toString() + "\n" + Staff.SPACE_BETWEEN_STAFFS + 
					enc.visualise(tss, getRhythmSymbolsCheckBox().isSelected()) + 
					footnotes.toString().substring(0, footnotes.lastIndexOf("\n"))
				);
				initializeTabViewer(encPath);
			} 
		}
	}


	/**
	 * Opens the TabViewer window.
	 * 
	 * @param encPath
	 */
	private void initializeTabViewer(String encPath) {
		JFrame tablatureFrame = new JFrame();
		tablatureFrame.setSize(FRAME_DIMS[0], FRAME_DIMS[1]);
		tablatureFrame.setJMenuBar(getTabViewerMenubar(encPath));
		tablatureFrame.setContentPane(getTabViewerPanel());
		tablatureFrame.setTitle("TabViewer");
		tablatureFrame.setVisible(true);
		tablatureFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}


	/**
	 * Creates the JMenuBar for the TabViewer.
	 * 
	 * @param encPath
	 * @return
	 */
	private JMenuBar getTabViewerMenubar(String encPath) {
		JMenuBar tablatureWindowMenubar = new JMenuBar();
		
		// File
		JMenu fileMenu = new JMenu("File");
		tablatureWindowMenubar.add(fileMenu);   
		// File > Save
		JMenuItem saveFile = new JMenuItem("Save");
		fileMenu.add(saveFile);
		saveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFileAction(getEncodingArea().getText(), encPath);
			}
		});

		// Edit
		JMenu editMenu = new JMenu("Edit");
		tablatureWindowMenubar.add(editMenu);
		// Edit > Select all
		JMenuItem selectAll = new JMenuItem("Select all");
		editMenu.add(selectAll);
		selectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getTabArea().selectAll();
			}
		});
		return tablatureWindowMenubar;
	}


	private JPanel getTabViewerPanel() {			
		JPanel tabViewerPanel = new JPanel();
		tabViewerPanel.setLayout(null);
		tabViewerPanel.setSize(new Dimension(PANEL_DIMS[0], PANEL_DIMS[1]));

		// Tab area with scroll pane
		JScrollPane tabAreaScrollPane = 
			new JScrollPane(getTabArea(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// See encodingAreaScrollPane.setBounds() for coordinates
		tabAreaScrollPane.setBounds(new Rectangle(15, 115-100, 676, 430+100));
		tabViewerPanel.add(tabAreaScrollPane, null);
		return tabViewerPanel;
	}


	private JScrollPane getTabViewerPane() { // alternative for getTabViewerPanel()
		return new JScrollPane(getTabArea(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}


	/**
	 * Visualises the encoding as tablature. 
	 * 
	 * @return 
	 */
	@Deprecated // moved to Encoding
	private String visualise(Encoding enc) {
		String tab = "";
		
		String ss = SymbolDictionary.SYMBOL_SEPARATOR;
		String sp = ConstantMusicalSymbol.SPACE.getEncoding();
		String sbi = SymbolDictionary.SYSTEM_BREAK_INDICATOR;

		String cleanEnc = enc.getCleanEncoding();
//		String cleanEnc = encoding.getCleanEncoding();
		TabSymbolSet tss = enc.getTabSymbolSet();
//		TabSymbolSet tss = encoding.getTabSymbolSet();

		// Search all systems one by one
		int sbiIndex = -1;
		int nextSbiIndex = cleanEnc.indexOf(sbi, sbiIndex + 1);
		while (sbiIndex + 1 != nextSbiIndex) { 
			RhythmSymbol prevRhythmSymbol = null;
			Staff staff = new Staff(enc.getStaffLength());
			int segment = 0;
			String currSysEncoding = cleanEnc.substring(sbiIndex + 1, nextSbiIndex);
			// Check for each system the encoded symbols one by one and for each encoded symbol 
			// add its tablature representation to staff 
			int ssIndex = -1;
			int nextSsIndex = currSysEncoding.indexOf(ss, ssIndex);
			while (nextSsIndex != -1) {
				String encodedSymbol = currSysEncoding.substring(ssIndex + 1, nextSsIndex);
				int nextNextSsIndex = currSysEncoding.indexOf(ss, nextSsIndex + 1);
				// nextEncodedSymbol is needed for b, c, and d below and can exist for all encoded 
				// symbols except for the last--i.e., as long as nextNextSsIndex is not -1
				String nextEncodedSymbol = null;
				if (nextNextSsIndex != -1) {
					nextEncodedSymbol = currSysEncoding.substring(nextSsIndex + 1, nextNextSsIndex);
				}
				// a. Add ConstantMusicalSymbol?
				if (ConstantMusicalSymbol.getConstantMusicalSymbol(encodedSymbol) != null) {
					ConstantMusicalSymbol c = ConstantMusicalSymbol.getConstantMusicalSymbol(encodedSymbol);
					staff.addConstantMusicalSymbol(encodedSymbol, segment);
					segment = segment + c.getSymbol().length();
				}
				// b. Add TabSymbol?
				else if (TabSymbol.getTabSymbol(encodedSymbol, tss) != null) { 
					TabSymbol t = TabSymbol.getTabSymbol(encodedSymbol, tss);
					if (frenchTabRadioButton.isSelected()) {   
						staff.addTabSymbolFrench(t, segment); 
					}
					else if (italianTabRadioButton.isSelected()) {
						staff.addTabSymbolItalian(t, segment);
					}
					else if (spanishTabRadioButton.isSelected()) {
						staff.addTabSymbolSpanish(t, segment);
					}
					else if (germanTabRadioButton.isSelected()) {
						// TODO 
					} 
					// Is encodedSymbol followed by a space and not by another TS--i.e., is it the 
					// last TS of a vertical sonority? Increment segment
					// NB: LAYOUT RULE 4 guarantees that a vertical sonority is always followed by a
					// space, meaning that nextEncodedSymbol always exists if encodedSymbol is a TS
					if (nextEncodedSymbol.equals(sp)) {
						segment++;
					}
				}
				// c. Add RhythmSymbol?
				else if (RhythmSymbol.getRhythmSymbol(encodedSymbol) != null) {
					RhythmSymbol r = RhythmSymbol.getRhythmSymbol(encodedSymbol);
					boolean showBeam = true;
					// rhythmSymbolsCheckBox not selected? Add RS; always add any beam
					if (!rhythmSymbolsCheckBox.isSelected()) {
						staff.addRhythmSymbol(r, segment, showBeam);    
					}
					// rhythmSymbolsCheckBox selected? Add RS only if r is not equal to 
					// previousRhythmSymbol; never add any beam
					else {
						// Compare r with prevRhythmSymbol; if prevRhythmSymbol is null or if they
						// do not have the same duration: add r to staff
						// NB: because of possibly present beams, direct comparison does not work: an RS and 
						// its beamed variant are considered inequal because they are defined as two different 
						// objects
						showBeam = false;
						if (prevRhythmSymbol == null) {
							staff.addRhythmSymbol(r, segment, showBeam);
						}
						else {
							if (r.getDuration() != prevRhythmSymbol.getDuration()) {
								staff.addRhythmSymbol(r, segment, showBeam);
							}
						}
					}
					// Is encodedSymbol followed by a space and not by a TS--i.e., does encodedSymbol
					// represent a rest? Increment segment
					// NB: LAYOUT RULE 5 guarantees that a rest is always followed by a space, 
					// meaning that nextEncodedSymbol always exists if encodedSymbol is a RS
					if (nextEncodedSymbol.equals(sp)) {
						segment ++;
					}
					prevRhythmSymbol = r;
				}     
				// d. Add MensurationSign?
				else if (MensurationSign.getMensurationSign(encodedSymbol) != null) {
					MensurationSign m = MensurationSign.getMensurationSign(encodedSymbol);
					staff.addMensurationSign(m, segment);
					// Is encodedSymbol followed by a space and not by another MS--i.e., is
					// encodedSymbol the only or the last symbol of a (compound) MS? Increment segment
					// NB: LAYOUT RULE 6 guarantees that the last MS is always followed by a space,
					// meaning that nextEncodedSymbol always exists if encodedSymbol is a MS 
					if (nextEncodedSymbol.equals(sp)) {
						segment ++;
					}
				}
				// Prepare indices for next iteration inner while
				ssIndex = nextSsIndex;
				nextSsIndex = currSysEncoding.indexOf(ss, ssIndex + 1); 
			}
			// System traversed? Add to tablature; prepare indices for next iteration outer while
			System.out.println(staff.getStaff());
			System.out.println(staff.getNumberOfSegments());
//			System.exit(0);
			
			tab += staff.getStaff() + Staff.SPACE_BETWEEN_STAFFS;
			sbiIndex = nextSbiIndex;
			nextSbiIndex = cleanEnc.indexOf(sbi, sbiIndex + 1);
		}
		return tab;
	}


	private JScrollPane getEncodingAreaScrollPane() {
//		if (encodingAreaScrollPane == null) {
//			encodingAreaScrollPane = 
		JScrollPane encodingAreaScrollPane = 		
			new JScrollPane(getEncodingArea(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		encodingAreaScrollPane.setBounds(new Rectangle(15, 115, 676, 430));
//		}
		return encodingAreaScrollPane;
	}


	private void prepareTabViewer() {
		getEncodingArea().setHighlighter(getHilit());
//		getEncodingArea().setHighlighter(hilit);
//		encodingArea.setHighlighter(hilit);
		ButtonGroup tabButtons = new ButtonGroup();
		tabButtons.add(frenchTabRadioButton);
		tabButtons.add(italianTabRadioButton);
		tabButtons.add(spanishTabRadioButton);
		tabButtons.add(germanTabRadioButton);
		tabButtons.add(tabCodeRadioButton);
		frenchTabRadioButton.setSelected(true);
	}


	private JRadioButton getFrenchTabRadioButton() {
//		if (frenchTabRadioButton == null) {
		frenchTabRadioButton = new JRadioButton("French tablature");
		frenchTabRadioButton.setBounds(new Rectangle(99, 42, 106, 16));
//		frenchTabRadioButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				// TODO Auto-generated Event stub actionPerformed()
//			}
//		});
//		}
		return frenchTabRadioButton;
	}


	private JRadioButton getItalianTabRadioButton() {
//		if (italianTabRadioButton == null) {
		italianTabRadioButton = new JRadioButton("Italian tablature");
		italianTabRadioButton.setBounds(new Rectangle(207, 42, 106, 16));
//		italianTabRadioButton.addActionListener(new ActionListener() {
//	    	public void actionPerformed(ActionEvent e) {
//	    		// TODO Auto-generated Event stub actionPerformed()
//	    	}
//	    });
//		}
	    return italianTabRadioButton;
	}


	private JRadioButton getSpanishTabRadioButton() {
//		if (spanishTabRadioButton == null) {
		spanishTabRadioButton = new JRadioButton("Spanish tablature");
		spanishTabRadioButton.setBounds(new Rectangle(315, 42, 106, 16));
//		spanishTabRadioButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				// TODO Auto-generated Event stub actionPerformed()
//			}
//		});
//		}
		return spanishTabRadioButton;
	}


	private JRadioButton getGermanTabRadioButton() {
//		if (germanTabRadioButton == null) {
		germanTabRadioButton = new JRadioButton("German tablature");
		germanTabRadioButton.setBounds(new Rectangle(423, 42, 106, 16));
//		germanTabRadioButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				// TODO Auto-generated Event stub actionPerformed()
//			}
//		});
//		}
		return germanTabRadioButton;
	}


	private JRadioButton getTabCodeRadioButton() {
//		if (tabCodeRadioButton == null) {
		tabCodeRadioButton = new JRadioButton("TabCode");
		tabCodeRadioButton.setBounds(new Rectangle(531, 42, 106, 16));
//		tabCodeRadioButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				// TODO Auto-generated Event stub actionPerformed()
//			}
//		});
//		}
		return tabCodeRadioButton;
	}


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


	private JButton getViewButton(String encPath) {
		JButton viewButton = new JButton();
		viewButton.setBounds(new Rectangle(600, 559, 91, 31));
		viewButton.setText("View");
		viewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewButtonAction(getEncodingArea().getText(), encPath);
//				viewButtonAction(encPath);
			}
		});
		return viewButton;
	}

}