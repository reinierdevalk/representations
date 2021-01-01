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
import tbp.*;


public class TabViewer extends JFrame{

	private static final long serialVersionUID = 1L;
	private Highlighter hilit = new DefaultHighlighter(); // 2 methods
	private JTextArea encodingArea; // 5 methods
	private JTextArea tabArea; // 3 methods
	private JLabel pieceInfoLabel; // 2 methods
	private JLabel upperErrorMessageLabel; // 2 methods
	private JLabel lowerErrorMessageLabel; // 2 methods
	private JRadioButton frenchTabRadioButton; // 2 methods
	private JRadioButton italianTabRadioButton; // 2 methods
	private JRadioButton spanishTabRadioButton; // 2 methods
	private JRadioButton germanTabRadioButton; // 2 methods
	private JRadioButton tabCodeRadioButton; // 1 method
	private JCheckBox rhythmSymbolsCheckBox; // 2 methods 


	public static void main(String[] args) {
		String encPath = null;
		if (args.length != 0) {
			encPath = args[0];
		}
		TabViewer gui = new TabViewer(encPath);
		gui.setVisible(true);
	}


	public TabViewer(String encPath) {
		super();
		initialize(encPath);
	}


	/**
	 * Initialises the GUI.
	 * 
	 * @param encFile
	 */
	private void initialize(String encPath) {
		this.setSize(717, 672);
		this.setJMenuBar(getEncodingViewerMenubar(encPath));
		this.setContentPane(getEncodingViewerPanel(encPath));
		this.setTitle("EncodingViewer");
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
				saveFileAction(encPath);
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
		encodingViewerPanel.setSize(new Dimension(586, 413));

		// Encoding labels
		JLabel pieceLabel = new JLabel("Encoding:");
		pieceLabel.setBounds(new Rectangle(15, 15, 81, 16));
		encodingViewerPanel.add(pieceLabel, null);
		pieceInfoLabel = new JLabel();
		pieceInfoLabel.setBounds(99, 17, 592, 14);
		encodingViewerPanel.add(pieceInfoLabel);    

		// View-as label and tablature radio buttons
		JLabel viewAsLabel = new JLabel("View as:");
		viewAsLabel.setBounds(new Rectangle(15, 42, 81, 16));
		encodingViewerPanel.add(viewAsLabel, null);
		frenchTabRadioButton = new JRadioButton("French tablature");
		frenchTabRadioButton.setBounds(new Rectangle(99, 42, 106, 16));
		encodingViewerPanel.add(frenchTabRadioButton, null);
		italianTabRadioButton = new JRadioButton("Italian tablature");
		italianTabRadioButton.setBounds(new Rectangle(207, 42, 106, 16));
		encodingViewerPanel.add(italianTabRadioButton, null);
		spanishTabRadioButton = new JRadioButton("Spanish tablature");
		spanishTabRadioButton.setBounds(new Rectangle(315, 42, 106, 16));
		encodingViewerPanel.add(spanishTabRadioButton, null);
		germanTabRadioButton = new JRadioButton("German tablature");
		germanTabRadioButton.setBounds(new Rectangle(423, 42, 106, 16));
		encodingViewerPanel.add(germanTabRadioButton, null);
		tabCodeRadioButton = new JRadioButton("TabCode");
		tabCodeRadioButton.setBounds(new Rectangle(531, 42, 106, 16));
		encodingViewerPanel.add(tabCodeRadioButton, null);
		ButtonGroup tabButtons = new ButtonGroup();
		tabButtons.add(frenchTabRadioButton);
		tabButtons.add(italianTabRadioButton);
		tabButtons.add(spanishTabRadioButton);
		tabButtons.add(germanTabRadioButton);
		tabButtons.add(tabCodeRadioButton);
		frenchTabRadioButton.setSelected(true);
		
		// Error labels
		JLabel errorLabel = new JLabel("Error:");
		errorLabel.setBounds(15, 69, 81, 16);
		encodingViewerPanel.add(errorLabel, null);   
		upperErrorMessageLabel = new JLabel();
		upperErrorMessageLabel.setBounds(new Rectangle(99, 69, 592, 16));
		upperErrorMessageLabel.setForeground(Color.RED); 
		encodingViewerPanel.add(upperErrorMessageLabel, null);
		lowerErrorMessageLabel = new JLabel();
		lowerErrorMessageLabel.setBounds(new Rectangle(99, 88, 592, 16));
		lowerErrorMessageLabel.setForeground(Color.RED);
		encodingViewerPanel.add(lowerErrorMessageLabel, null);

		// Scroll pane
		encodingArea = new JTextArea();
		encodingArea.setLineWrap(true); // necessary because of scroll bar
		encodingArea.setBounds(new Rectangle(15, 105, 571, 76));
		encodingArea.setEditable(true);
		encodingArea.setFont(new Font("Courier New", Font.PLAIN, 12));
		encodingArea.setHighlighter(hilit);
		JScrollPane encodingAreaScrollPane =
			new JScrollPane(encodingArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		encodingAreaScrollPane.setBounds(new Rectangle(15, 115, 676, 430));
		encodingViewerPanel.add(encodingAreaScrollPane, null);
		
		// Repeated rhythm symbols checkbox
		rhythmSymbolsCheckBox = new JCheckBox();
		rhythmSymbolsCheckBox.setBounds(new Rectangle(15, 559, 211, 16));
		rhythmSymbolsCheckBox.setActionCommand("Show all rhythm symbols");
		rhythmSymbolsCheckBox.setText("Ignore repeated rhythm symbols");
		encodingViewerPanel.add(rhythmSymbolsCheckBox, null);
		
		// View button
		JButton viewButton = new JButton();
		viewButton.setBounds(new Rectangle(600, 559, 91, 31));
		viewButton.setText("View");
		viewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewButtonAction(encPath);
			}
		});
		encodingViewerPanel.add(viewButton, null);

		return encodingViewerPanel;
	}


	/**
	 * Loads the content of a file into encodingArea. 
	 * 
	 * This is the action performed when clicking File > Open from the EncodingViewer menu.
	 * 
	 * @param encPath The path from which to open the file.
	 */
	private void openFileAction(String encPath) {
		// TODO encodingFile is now hardcoded; make possible to select file from menu
		if (encPath == null) {
			String prefix = "F:/research/data/data/encodings/"; // TODO to line above
			encPath = prefix;
			
			// Test
			encPath += "test/testpiece.tbp";
//			path += "test/test_get_meter_info.tbp";
//			path =  "F:/research/publications/conferences-workshops/2019-ISMIR/paper/test/tab/3610_033_inter_natos_mulierum_morales_T-rev.tbp";
//			path =  "F:/research/publications/conferences-workshops/2019-ISMIR/paper/test/tab/3618_041_benedictus_from_missa_de_l_homme_arme_morales_T.tbp";
//			path =  "F:/research/projects/byrd/test/il_me_souffit-short.tbp";
//			path =  "F:/research/projects/byrd/test/pleni.tbp";
			
			// Need to be double-checked
//			path += "newsidler-1536_6-mein_einigs_a.tbp";
//			path += "Newsidler 1536 - Mein hertz alzeyt hat gross verlangen.tbp";
//			path += "Ochsenkun 1558 - Cum Sancto spiritu.tbp";  	
//			path += "Barbetta 1582 - Martin menoit.tbp";
//			path += "Da Crema 1546 - Il nest plaisir.tbp";
//			path += "De Narbaez 1538 - MIlle regres.tbp";
//			path += "Heckel 1562 - Il est vne Fillete. [Tenor].tbp";
//			path += "Heckel 1562 - Il estoit vne fillete. Discant.tbp";
//			path += "Morlaye 1552 - LAs on peut iuger.tbp";
//			path += "Newsidler 1544 - Der hupff auf.tbp";
//			path += "Newsidler 1544 - Hie volget die Schlacht vor Bafia. Der Erst Teyl.tbp";
//			path += "Newsidler 1544 - Hie volget die Schlacht vor Bafia. Der ander Teyl der schlacht.tbp";
//			path += "Newsidler 1544 - Sula Bataglia.tbp";
//			path += "Ochsenkun 1558 - Benedicta es coelorum, Prima pars.tbp";
//			path += "Ochsenkun 1558 - Gott alls in allem wesentlich.tbp";
//			path += "Ochsenkun 1558 - Pater Noster, Prima pars.tbp"; 
//			path += "Ochsenkun 1558 - Praeter rerum seriem, Prima pars.tbp";
//			path += "Ochsenkun 1558 - Stabat mater dolorosa, Prima pars.tbp";
//			path += "Phalese 1546 - Martin menuyt de Iennequin.tbp";
//			path += "Spinacino 1507 - LA Bernardina de Iosquin.tbp";
			
			// Checked and ready for processing
			// 3vv
//			path += "tab-int/3vv/" + "newsidler_1536-disant_adiu.tbp";
//			path += "tab-int/3vv/" + "newsidler_1536-mess_pensees.tbp";
//			path += "tab-int/3vv/" + "pisador-1552_7-pleni_de.tbp";
//			path += "tab-int/3vv/" + "judenkuenig-1523_2-elslein_liebes.tbp";
//			path += "tab-int/3vv/" + "newsidler-1544_2-nun_volget.tbp";
//			path += "tab-int/3vv/" + "phalese-1547-tant_que-a3.tbp"; 

			// 4vv
//			path += "tab-int/4vv/" + "ochsenkun_1558_-_absolon_fili.tbp";
//			path += "tab-int/4vv/" + "ochsenkun_1558_-_in_exitu.tbp";
//			path += "tab-int/4vv/" + "ochsenkun_1558_-_qui_habitat.tbp";
//			path += "tab-int/4vv/" + "rotta-1546_15-bramo_morir.tbp";
//			path += "tab-int/4vv/" + "phalese_1547_-_tant_que_a4.tbp";
//			path += "tab-int/4vv/" + "ochsenkun_1558_-_herr_gott.tbp";
//			path += "tab-int/4vv/" + "abondante-1548_1-mais_mamignone.tbp";
//			path += "tab-int/4vv/" + "phalese_1563_-_las_on.tbp";
//			path += "tab-int/4vv/" + "barbetta_1582_-_il_nest.tbp";
//			path += "tab-int/4vv/" + "phalese_1563_-_il_estoit.tbp";
//			path += "tab-int/4vv/" + "BSB-mus.ms._272-mille_regres.tbp";

			// 5vv
//			path += "tab-int/5vv/" + "adriansen_1584_-_dvn_si.tbp";
//			path += "tab-int/5vv/" + "ochsenkun_1558_-_inuiolata_integra.tbp";
			
			// Byrd
//			path += "byrd-int/4vv/as_caesar_wept-II.tbp";

			// JosquIntab
//			path = "F:/research/data/data/josquintab/tab/" + "5256_05_inviolata_integra_desprez-2.tbp";
//			path = "F:/research/data/data/josquintab/tab/" + "5263_12_in_exitu_israel_de_egipto_desprez-3.tbp";
		}
		File encFile = new File(encPath);
		
		pieceInfoLabel.setText(encFile.getName());
		String rawEncoding = "";
		try {
			rawEncoding = new String(Files.readAllBytes(Paths.get(encFile.getAbsolutePath())));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		encodingArea.setText(rawEncoding);
	}


	/**
	 * Saves the encoding in the encodingArea in the file it was loaded from.
	 * 
	 * This is the action performed when clicking File > Save from the EncodingViewer and 
	 * TabViewer menu.
	 * 
	 * @param encPath The path to save the file to. 
	 * 
	 */
	private void saveFileAction(String encPath) {  	
		String encodingStr = encodingArea.getText();

		// Handle any returns added to encoding (which will be "\n" and not "\r\n") by replacing
		// them with "\r\n"
		// a. List all indices of the \ns not preceded by \rs
		List<Integer> indicesOfLineBreaks = new ArrayList<Integer>(); 
		for (int i = 0; i < encodingStr.length(); i++) {
			String currentChar = encodingStr.substring(i, i + 1);
			if (currentChar.equals("\n")) {
				// NB: previousChar always exists as the char at index 0 in encoding will never be a \n
				String previousChar = encodingStr.substring(i - 1, i);	
				if (!previousChar.equals("\r")) {
					indicesOfLineBreaks.add(i);
				}
			}
		}  	
		// b. Replace all \ns not preceded by \rs in the encoding by \n\rs and store the file
		for (int i = indicesOfLineBreaks.size() - 1; i >= 0; i--) {
			int currentIndex = indicesOfLineBreaks.get(i);
			encodingStr = encodingStr.substring(0, currentIndex) + "\r" + encodingStr.substring(currentIndex);
		}
//		ToolBox.storeTextFile(encodingStr, encodingFile); 
		try {
			Files.write(Paths.get(encPath), encodingStr.getBytes());
//			Files.write(Paths.get(encodingFile.getAbsolutePath()), encodingStr.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	 * @param encPath
	 */ 
	private void viewButtonAction(String encPath) { 
		final int firstErrorCharIndex = 0;
		final int lastErrorCharIndex = 1;
		final int errorStringIndex = 2;
		final int ruleStringIndex = 3;
		
		// 1. Create an unchecked encoding
		// The first time the viewbutton is clicked, encodingArea.getText() will always be 
		// exactly as in the file that is loaded because it is set as such in openFileAction().
		// Any next time, it will be exactly what is in the encodingArea (which now may have 
		// corrections compared to what is in the loaded file)
		String rawEncoding = encodingArea.getText();
		Encoding enc = new Encoding(rawEncoding, false);
		// a. If the encoding contains metadata errors: place error message
		if (enc.getHasMetadataErrors()) {
			upperErrorMessageLabel.setText(Encoding.METADATA_ERROR);
			lowerErrorMessageLabel.setText("");
		}
		// b. If the encoding contains no metadata errors: continue
		else {
			// 2. Check the encoding
			// Remove any remaining highlights and error messages
			hilit.removeAllHighlights();
			upperErrorMessageLabel.setText("(none)");
			lowerErrorMessageLabel.setText(null);
			// a. If the encoding contains encoding errors: place error messages and highlight
			if (enc.checkForEncodingErrors() != null) { // needs rawEncoding, cleanEncoding, and infoAndSettings
				upperErrorMessageLabel.setText(enc.checkForEncodingErrors()[errorStringIndex]);
				lowerErrorMessageLabel.setText(enc.checkForEncodingErrors()[ruleStringIndex]);
				int hilitStartIndex = Integer.parseInt(enc.checkForEncodingErrors()[firstErrorCharIndex]);
				int hilitEndIndex = Integer.parseInt(enc.checkForEncodingErrors()[lastErrorCharIndex]);
				Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
				try { 
					hilit.addHighlight(hilitStartIndex, hilitEndIndex, painter);
				} catch (BadLocationException e) {  
					System.err.println("BadLocationException: " + e.getMessage());
				}
			}
			// b. If the encoding contains no encoding errors: show the tablature in a new window 
			else {
				String allFootnotes = "";
				for (String footnote : enc.getFootnotes()) {
					allFootnotes = allFootnotes.concat(footnote + "\n");
				}
//				List<String> infoAndSettings = enc.getInfoAndSettings();
				
				TabSymbolSet tss = null;
				if (frenchTabRadioButton.isSelected()) {   
					tss = TabSymbolSet.FRENCH_TAB; 
				}
				else if (italianTabRadioButton.isSelected()) {
					tss = TabSymbolSet.ITALIAN_TAB;
				}
				else if (spanishTabRadioButton.isSelected()) {
					tss = TabSymbolSet.SPANISH_TAB;
				}
				else if (germanTabRadioButton.isSelected()) {
					// TODO 
				}

				tabArea = getTabArea();
				tabArea.setText(
					enc.getMetaDataFormatted() + 
//					infoAndSettings.get(Encoding.AUTHOR_INDEX) + "\n" + 
//					infoAndSettings.get(Encoding.TITLE_INDEX) + "\n" + 
//					infoAndSettings.get(Encoding.SOURCE_INDEX) + "\n" + "\n" + 
					Staff.SPACE_BETWEEN_STAFFS + 
						enc.visualise(tss, rhythmSymbolsCheckBox.isSelected()) + 
//					SPACE_BETWEEN_STAFFS + visualise(enc)
					allFootnotes
				);
				openTablatureWindow(encPath);
			} 
		}
	}


	/**
	 * 
	 * @param encPath
	 */
	private void openTablatureWindow(String encPath) {
		JFrame tablatureFrame = new JFrame("TabViewer");
		tablatureFrame.setSize(800, 500);
		tablatureFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		tablatureFrame.setJMenuBar(getTabViewerMenubar(encPath));
		tablatureFrame.setContentPane(getTabAreaScrollPane());
		tablatureFrame.setVisible(true);
	}


	/**
	 * 
	 * @param encPath
	 * @return
	 */
	private JMenuBar getTabViewerMenubar(String encPath) {
		JMenuBar tablatureWindowMenubar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		tablatureWindowMenubar.add(fileMenu);   

		JMenuItem saveFile = new JMenuItem("Save");
		fileMenu.add(saveFile);
		saveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFileAction(encPath);
			}
		});

		JMenu editMenu = new JMenu("Edit");
		tablatureWindowMenubar.add(editMenu);

		JMenuItem selectAll = new JMenuItem("Select all");
		editMenu.add(selectAll);
		selectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectAllEditAction();
			}
		});

		return tablatureWindowMenubar;
	}


	/**
	 * Saves the encoding in the encodingArea in the file it was loaded from.
	 * 
	 * This is the action performed when clicking Edit > Select all from TabViewer menu.
	 * 
	 */
	private void selectAllEditAction() {
		tabArea.selectAll();
	}


	private JTextArea getTabArea() {
		if (tabArea == null) {
			tabArea = new JTextArea();
			tabArea.setBounds(new Rectangle(15, 240, 571, 136));
			tabArea.setEditable(false);
			tabArea.setFont(new Font("Courier New", Font.PLAIN, 12));    
		}
		return tabArea;
	}


	private JScrollPane getTabAreaScrollPane() {			
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


	/**
	 * Returns encodingArea   
	 *    
	 * @return javax.swing.JTextArea      
	 */
	private JTextArea getEncodingArea() {
		encodingArea = new JTextArea();
		encodingArea.setLineWrap(true); //necessary because of scroll bar
		encodingArea.setBounds(new Rectangle(15, 105, 571, 76));
		encodingArea.setEditable(true);
		encodingArea.setFont(new Font("Courier New", Font.PLAIN, 12)); 
		return encodingArea;
	}


	private void prepareTabViewer() {    
		encodingArea.setHighlighter(hilit);
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


	private JCheckBox getRhythmSymbolsCheckBox() {
//		if (rhythmSymbolsCheckBox == null) {
		rhythmSymbolsCheckBox = new JCheckBox();
		rhythmSymbolsCheckBox.setBounds(new Rectangle(15, 559, 211, 16));
		rhythmSymbolsCheckBox.setActionCommand("Show all rhythm symbols");
		rhythmSymbolsCheckBox.setText("Ignore repeated rhythm symbols");
//		rhythmSymbolsCheckBox.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				// TODO Auto-generated Event stub actionPerformed()
//			}
//		});
//		}
		return rhythmSymbolsCheckBox;
	}


	private JButton getViewButton(String encPath) {
		JButton viewButton = new JButton();
		viewButton.setBounds(new Rectangle(600, 559, 91, 31));
		viewButton.setText("View");
		viewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewButtonAction(encPath);
			}
		});
		return viewButton;
	}

}