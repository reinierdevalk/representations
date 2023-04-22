package tbp;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
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
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import path.Path;
import tbp.Encoding.Stage;
import tbp.TabSymbol.TabSymbolSet;
import tools.ToolBox;

public class Viewer extends JFrame{
	private static final long serialVersionUID = 1L;

	private static final Font FONT = new Font("Courier New", Font.PLAIN, 12);
	private static final String TOOL_NAME = "tab+Editor";
	private static final String ASCII_EXTENSION = ".tab";
	private static final String TABCODE_EXTENSION = ".tc";
	private static final String MEI_EXTENSION = ".xml";
	private static final String[] TITLE = 
		new String[]{"untitled", Encoding.EXTENSION, " - " + TOOL_NAME};
	public static final String METADATA_ERROR = 
		"METADATA ERROR -- Check for missing or misplaced curly brackets.";

	private static final int H_MARGIN = 15;
	private static final int V_MARGIN = 15;
	private static final int PANEL_W = 900;
	private static final int ENC_PANEL_H = 200;
	private static final int TAB_PANEL_H = 300;
	private static final int LABEL_W = 90;
	private static final int LABEL_H = V_MARGIN;
	private static final int BUTTON_W = LABEL_W;
	private static final int BUTTON_H = 2*V_MARGIN;
	private static final int V_CORRECTION = 157;
	private static final Integer[] PANEL_DIMS = new Integer[]{H_MARGIN + PANEL_W + H_MARGIN, -1};
	private static final Integer[] FRAME_DIMS = new Integer[]{
		H_MARGIN/2 + PANEL_DIMS[0] + H_MARGIN/2, 
		V_MARGIN + ENC_PANEL_H + V_MARGIN + TAB_PANEL_H + V_MARGIN + V_CORRECTION
	};

	private Highlighter highlighter;
//	private JLabel pieceLabel;
	private ButtonGroup tabTypeButtonGroup;
//	private JLabel upperErrorLabel;
//	private JLabel lowerErrorLabel;
	private JTextArea encodingTextArea;
	private JTextArea tabTextArea;
	private JCheckBox rhythmSymbolsCheckBox;
//	private JButton viewButton;	
//	private JMenuBar menuBar;
//	private JPanel editorPanel;
	private JFileChooser fileChooser;
	private File file;


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
		// https://www.codespeedy.com/how-to-add-multiple-panels-in-jframe-in-java/
		setTitle(TITLE[0] + TITLE[1] + TITLE[2]);
		setBounds(0, 0, FRAME_DIMS[0], FRAME_DIMS[1]);
		setLayout(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		// 
		setJMenuBar(makeMenuBar());
		setFileChooser(); 
		setFile(null);

		// Hierarchy
		// 0. JFrame -> 1. Container -> 2. JPanels

		// 1. Container
		Container cp = getContentPane();

		// 2. JPanels
		// a. Encoding panel
		JPanel encPanel = makeJPanel(
			null, new Rectangle(H_MARGIN, V_MARGIN, PANEL_W, ENC_PANEL_H), "Encoding"
		);
		
		setHighlighter();
		setEncodingTextArea();
		JScrollPane encScrollPane = makeJScrollPane(
			getEncodingTextArea(), 
			new Rectangle(H_MARGIN, 2*V_MARGIN, PANEL_W - 2*H_MARGIN, ENC_PANEL_H - 3*V_MARGIN)
		);
		encPanel.add(encScrollPane, null);
		
		// b. Tablature panel
		JPanel tabPanel = makeJPanel(
			null, new Rectangle(H_MARGIN, 2*V_MARGIN + ENC_PANEL_H, PANEL_W, TAB_PANEL_H + 6*V_MARGIN), 
			"Tablature"
		);

		setTabTextArea();
		JScrollPane tabScrollPane = makeJScrollPane(
			getTabTextArea(), 
			new Rectangle(H_MARGIN, 2*V_MARGIN, PANEL_W - 2*H_MARGIN, TAB_PANEL_H - 3*V_MARGIN)
		);
		tabPanel.add(tabScrollPane, null);

		JPanel stylePanel = makeJPanel(
			null, 
			new Rectangle(H_MARGIN, TAB_PANEL_H, (PANEL_W / 3) - H_MARGIN, 5*V_MARGIN),
			"Tablature style"
		);
		setTabTypeButtonGroup();
		for (AbstractButton b : Collections.list(getTabTypeButtonGroup().getElements())) {
			stylePanel.add(b, null);
		}
		tabPanel.add(stylePanel, null);
		
		JPanel rsPanel = makeJPanel(
			null, 
			new Rectangle(H_MARGIN + (PANEL_W / 3), TAB_PANEL_H, (PANEL_W / 3) - H_MARGIN, 5*V_MARGIN),
			"Rhythm flags"
		);
		setRhythmSymbolsCheckBox();
		rsPanel.add(getRhythmSymbolsCheckBox(), null);
		tabPanel.add(rsPanel, null);
		
//		setViewButton();
//		tabPanel.add(getViewButton());
		JButton viewButton = makeJButton(
			new Rectangle(PANEL_W - BUTTON_W - H_MARGIN, TAB_PANEL_H, BUTTON_W, BUTTON_H), 
			"View"
		);
		viewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewButtonAction();
			}
		});
		tabPanel.add(viewButton);

		cp.add(encPanel);
		cp.add(tabPanel);
				
		setVisible(true);
	}


	private JTextArea makeJTextArea(String content, Rectangle bounds, Highlighter hl) {
		JTextArea ta = new JTextArea();
		// If there is a JScrollPane, the JTextArea's bounds are overridden by the JScrollPane's 
		if (bounds != null) {
			ta.setBounds(bounds);
		}
		ta.setLineWrap(true); // necessary because of JScrollPane
		ta.setEditable(true);
		ta.setFont(FONT);
		ta.setText(content);
		ta.setHighlighter(hl);
		return ta;
	}


	private JPanel makeJPanel(LayoutManager lm, Rectangle bounds, String borderText) {
		JPanel p = new JPanel();
		p.setLayout(lm);
		p.setBounds(bounds);
		p.setBorder(BorderFactory.createTitledBorder(borderText));
		return p;
	}


	private JScrollPane makeJScrollPane(JTextArea ta, Rectangle bounds) {
		JScrollPane sp = 
			new JScrollPane(ta, 
			ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setBounds(bounds);
		return sp;
	}


	//////////////////////////////
	//
	//  S E T T E R S  
	//  for instance variables
	//
	private void setHighlighter() {
		highlighter = new DefaultHighlighter();
	}


	private void setTabTypeButtonGroup() {
		String[] t = new String[]{
			TabSymbolSet.FRENCH.getType(), 
			TabSymbolSet.ITALIAN.getType(),
			TabSymbolSet.SPANISH.getType(), 
			TabSymbolSet.NEWSIDLER_1536.getType()};
		Rectangle[] bounds = new Rectangle[]{
			new Rectangle(H_MARGIN, 2*V_MARGIN, LABEL_W, LABEL_H),
			new Rectangle(H_MARGIN, 2*V_MARGIN + LABEL_H, LABEL_W, LABEL_H),
			new Rectangle(H_MARGIN + LABEL_W, 2*V_MARGIN, LABEL_W, LABEL_H),
			new Rectangle(H_MARGIN + LABEL_W, 2*V_MARGIN + LABEL_H, LABEL_W, LABEL_H)
		};
		Boolean[] selected = new Boolean[]{true, false, false, false};
		Boolean[] enabled = new Boolean[]{true, true, true, false};
		tabTypeButtonGroup = makeButtonGroup(t, bounds, selected, enabled);
	}


	private ButtonGroup makeButtonGroup(String[] t, Rectangle[] bounds, Boolean[] selected, Boolean[] enabled) {
		ButtonGroup bg = new ButtonGroup();
		for (int i = 0; i < bounds.length; i++) {
			JRadioButton rb = new JRadioButton(t[i]);
			rb.setBounds(bounds[i]);
			rb.setSelected(selected[i]);
			rb.setEnabled(enabled[i]);
			bg.add(rb);
		}
		return bg;
	}


	private void setEncodingTextArea() {
		encodingTextArea = makeJTextArea("", null, getHighlighter());
	}


	private void setTabTextArea() {
		tabTextArea = makeJTextArea("", null, null);
	}


	private void setRhythmSymbolsCheckBox() {
		rhythmSymbolsCheckBox = makeJCheckBox(
			new Rectangle(H_MARGIN, 2*V_MARGIN, LABEL_W*2 + (LABEL_W / 2), LABEL_H),
			"Hide repeated rhythm flags"
		);
	}


	private JCheckBox makeJCheckBox(Rectangle bounds, String t) {
		JCheckBox cb = new JCheckBox();
		cb.setBounds(bounds);
		cb.setText(t);
		return cb;
	}


	private JButton makeJButton(Rectangle bounds, String t) {
		JButton b = new JButton();
		b.setBounds(bounds);
		b.setText(t);
		return b;
	}


	private JMenuBar makeMenuBar() {
		JMenuBar mb = new JMenuBar();

		Map<String, String> extensions = new LinkedHashMap<String, String>();
		extensions.put("ASCII tab", ASCII_EXTENSION);
		extensions.put("TabCode", TABCODE_EXTENSION);
		extensions.put("MEI", MEI_EXTENSION);

		// File
		JMenu fileM = new JMenu("File");
		for (String s : Arrays.asList("New", "Open", "Save", "Save as", "Import", "Export")) {
			// Add JMenuItem
			if (!Arrays.asList("Import", "Export").contains(s)) {
				JMenuItem mi = new JMenuItem(s);
				mi.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (s.equals("New")) {
							newFileAction();
						}
						else if (s.equals("Open")) {
							openFileAction();
						}
						else if (s.equals("Save")) {
							saveFileAction(Encoding.EXTENSION);
						}
						else if (s.equals("Save as")) {
							saveAsFileAction(Encoding.EXTENSION);
						}
					}
				});
				fileM.add(mi);
			}
			// Add JMenu with JMenuItems
			else {
				JMenu m = new JMenu(s);
				for (String s2 : (s.equals("Import") ? Arrays.asList("ASCII tab", "TabCode") : 
					Arrays.asList("ASCII tab", "MEI"))) {
					JMenuItem mi = new JMenuItem(s2);
					mi.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if (s.equals("Import")) {
								importFileAction(extensions.get(s2));
							}
							else if (s.equals("Export")) {
								exportFileAction(extensions.get(s2));
							}
						}
					});
					m.add(mi);
				}
				fileM.add(m);
			}
		}
		
//		JMenu importSubm = new JMenu("Import");
//		for (String s : Arrays.asList("ASCII tab", "TabCode")) {
//			JMenuItem mi = new JMenuItem(s);
//			mi.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					importFileAction(s.equals("ASCII tab") ? ASCII_EXTENSION : TABCODE_EXTENSION);
//				}
//			});
//			importSubm.add(mi);
//		}
//		fileM.add(importSubm);
//				
//		JMenu exportSubm = new JMenu("Export");
//		for (String s : Arrays.asList("ASCII tab", "MEI")) {
//			JMenuItem mi = new JMenuItem(s);
//			mi.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					exportFileAction(s.equals("ASCII tab") ? ASCII_EXTENSION : MEI_EXTENSION);
//				}
//			});
//			exportSubm.add(mi);
//		}
//		fileM.add(exportSubm);

		// Edit
		JMenu editM = new JMenu("Edit");
		JMenuItem selectAllMenuItem = new JMenuItem("Select all");
		selectAllMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getEncodingTextArea().requestFocus();
				getEncodingTextArea().selectAll();
			}
		});
		editM.add(selectAllMenuItem);

		mb.add(fileM);
		mb.add(editM);
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


	//////////////////////////////
	//
	//  G E T T E R S
	//  for instance variables
	//
	private Highlighter getHighlighter() {
		return highlighter;
	}


	private ButtonGroup getTabTypeButtonGroup() {
		return tabTypeButtonGroup;
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


	private JFileChooser getFileChooser() {
		return fileChooser;
	}


	private File getFile() {
		return file;
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
		setTextAreaContent(s, getEncodingTextArea());
		setTextAreaContent("", getTabTextArea());
	}


	private void openFileAction() {
		setTextAreaContent("", getTabTextArea());
		getFileChooser().setDialogType(JFileChooser.OPEN_DIALOG);
		getFileChooser().setDialogTitle("Open");
		// Set file type filter
		getFileChooser().setFileFilter(new FileNameExtensionFilter("tab+ (.tbp)", 
			Encoding.EXTENSION.substring(1)));
		// Remove any previous selection
		getFileChooser().setSelectedFile(new File(""));
		// Get file
		if (getFileChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = null;
			try {
				f = getFileChooser().getSelectedFile();
				BufferedReader br = new BufferedReader(new FileReader(getFileChooser().getSelectedFile()));						
			} catch (IOException e) {
				// 11:11
				// https://www.youtube.com/watch?v=Z8p_BtqPk78
				e.printStackTrace();
			}
			setFile(f);
			setTitle(f.getName() + " - " + TOOL_NAME);
			String rawEncoding = "";
			try {
				rawEncoding = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())));
//				rawEncoding = ToolBox.readTextFile(f);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			setTextAreaContent(rawEncoding, getEncodingTextArea());
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
		getFileChooser().setDialogTitle("Save as");
		// Set file type filter and suggested file name 
		if (extension.equals(Encoding.EXTENSION)) {
			getFileChooser().setFileFilter(new FileNameExtensionFilter("tab+ (.tbp)", extension.substring(1)));
			getFileChooser().setSelectedFile(getFile() == null ? new File("untitled" + extension): getFile());
		}
		else {
			getFileChooser().setFileFilter(new FileNameExtensionFilter("ASCII (" + ASCII_EXTENSION +")", extension.substring(1)));			
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
				
				
				f = getFileChooser().getSelectedFile();;
//				BufferedReader br = new BufferedReader(new FileReader(getFileChooser().getSelectedFile()));
//				BufferedReader br = new BufferedReader(new FileReader(new File("")));

//			} catch (IOException e) {
//				e.printStackTrace();
//			}
				
			if (getFile() == null) {
				setFile(f);
				setTitle(f.getName());
			}

			// Handle any returns added to encoding (which will be "\n" and not "\r\n") by 
			// replacing them with "\r\n"
			// 1. List all indices of the \ns not preceded by \rs
			List<Integer> indsOfLineBreaks = new ArrayList<Integer>(); 
			for (int i = 0; i < content.length(); i++) {
				String currChar = content.substring(i, i + 1);
				if (currChar.equals("\n")) {
					// NB: prevChar always exists as the char at index 0 in encoding will
					// never be a \n
					String prevChar = content.substring(i - 1, i);	
					if (!prevChar.equals("\r")) {
						indsOfLineBreaks.add(i);
					}
				}
			}
			// 2. Replace all \ns not preceded by \rs in the encoding by \n\rs and store the file
			for (int i = indsOfLineBreaks.size() - 1; i >= 0; i--) {
				int currInd = indsOfLineBreaks.get(i);
				content = content.substring(0, currInd) + "\r" + content.substring(currInd);
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
		setTextAreaContent("", getTabTextArea());
		getFileChooser().setDialogType(JFileChooser.OPEN_DIALOG);
		getFileChooser().setDialogTitle("Import");
		// Set file type filter
		if (extension.equals(TABCODE_EXTENSION)) {
			getFileChooser().setFileFilter(new FileNameExtensionFilter("TabCode (" + TABCODE_EXTENSION + ")", extension.substring(1)));
		}
		else if (extension.equals(ASCII_EXTENSION)) {
			getFileChooser().setFileFilter(new FileNameExtensionFilter("ASCII (" + ASCII_EXTENSION + ")", extension.substring(1)));
		}
		// Remove any previous selection
		getFileChooser().setSelectedFile(new File(""));
		if (getFileChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = null;
			try {
				f = getFileChooser().getSelectedFile();
				BufferedReader br = new BufferedReader(new FileReader(getFileChooser().getSelectedFile()));						
			} catch (IOException e) {
				// 11:11
				// https://www.youtube.com/watch?v=Z8p_BtqPk78
				e.printStackTrace();
			}
		}
	}


	private void exportFileAction(String extension) {
		getFileChooser().setDialogType(JFileChooser.SAVE_DIALOG);
		getFileChooser().setDialogTitle("Export");
		// Set file type filter
		if (extension.equals(".mei") || extension.equals(MEI_EXTENSION)) {
			getFileChooser().setFileFilter(new FileNameExtensionFilter("MEI (.mei, " + MEI_EXTENSION + ")", extension.substring(1)));
		}
		else if (extension.equals(ASCII_EXTENSION)) {
			getFileChooser().setFileFilter(new FileNameExtensionFilter("ASCII (" + ASCII_EXTENSION + ")", extension.substring(1)));
		}
		getFileChooser().setSelectedFile(new File(getFile().getAbsolutePath().replace(Encoding.EXTENSION, extension)));
		if (getFileChooser().showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
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
		Encoding enc = new Encoding(rawEnc, "", Stage.MINIMAL);
		// a. If the encoding contains metadata errors: place error message
		if (Encoding.checkForMetadataErrors(rawEnc)) {
//			getUpperErrorLabel().setText(METADATA_ERROR);
//			getLowerErrorLabel().setText("");
//			getErrorMessageLabel("upper").setText(Encoding.METADATA_ERROR);
//			getErrorMessageLabel("lower").setText("");
			JOptionPane optionPane = 
				new JOptionPane(METADATA_ERROR + "\n" + "", 
				JOptionPane.ERROR_MESSAGE);
//				JOptionPane optionPane = new JOptionPane("ErrorMsg", JOptionPane.ERROR_MESSAGE);
			JDialog dialog = optionPane.createDialog("ERROR");
			dialog.setAlwaysOnTop(true);
			dialog.setVisible(true);
		}
		// b. If the encoding contains no metadata errors: continue
		else {
			enc = new Encoding(rawEnc, "", Stage.METADATA_CHECKED);
			String cleanEnc = enc.getCleanEncoding();
			// 2. Check the encoding
			// Remove any remaining highlights and error messages
			getHighlighter().removeAllHighlights();
//			getUpperErrorLabel().setText("(none)");
//			getLowerErrorLabel().setText(null);
//			getErrorMessageLabel("upper").setText("(none)");
//			getErrorMessageLabel("lower").setText(null);
			// a. If the encoding contains encoding errors: place error messages and highlight
			String[] encErrs = Encoding.checkForEncodingErrors(rawEnc, cleanEnc, enc.getTabSymbolSet());
			if (encErrs != null) {
//				getUpperErrorLabel().setText(encErrs[errorStringIndex]);
//				getLowerErrorLabel().setText(encErrs[ruleStringIndex]);
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
				JOptionPane optionPane = 
					new JOptionPane(encErrs[errorStringIndex] + "\n" + encErrs[ruleStringIndex], 
					JOptionPane.ERROR_MESSAGE);
//				JOptionPane optionPane = new JOptionPane("ErrorMsg", JOptionPane.ERROR_MESSAGE);
				JDialog dialog = optionPane.createDialog("ERROR");
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);
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
				
				setTextAreaContent(enc.visualise(tss, getRhythmSymbolsCheckBox().isSelected(), true, true), getTabTextArea());
//				new Viewer(/*getFileName(Encoding.EXTENSION)*/getFile(), enc.visualise(tss, getRhythmSymbolsCheckBox().isSelected(), true, true), false);
			} 
		}
	}


	private void setTextAreaContent(String content, JTextArea ta) { 
		ta.setText(content);
		ta.setCaretPosition(0);
	}


	private void initFromWWW() {
        setTitle("JPANEL CREATION");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        //setting the bounds for the JFrame
        setBounds(100, 100, 645, 470);
        Border br = BorderFactory.createLineBorder(Color.black);
        Container c=getContentPane();
        //Creating a JPanel for the JFrame
        JPanel panel=new JPanel();
        JPanel panel2=new JPanel();
        JPanel panel3=new JPanel();
        JPanel panel4=new JPanel();
        //setting the panel layout as null
        panel.setLayout(null);
        panel2.setLayout(null);
        panel3.setLayout(null);
        panel4.setLayout(null);
        //adding a label element to the panel
        JLabel label=new JLabel("Panel 1");
        JLabel label2=new JLabel("Panel 2");
        JLabel label3=new JLabel("Panel 3");
        JLabel label4=new JLabel("Panel 4");
        label.setBounds(120,50,200,50);
        label2.setBounds(120,50,200,50);
        label3.setBounds(120,50,200,50);
        label4.setBounds(120,50,200,50);
        panel.add(label);
        panel2.add(label2);
        panel3.add(label3);
        panel4.add(label4);
        // changing the background color of the panel to yellow
        //Panel 1
        panel.setBackground(Color.yellow);
        panel.setBounds(10,10,300,200);
        //Panel 2
        panel2.setBackground(Color.red);
        panel2.setBounds(320,10,300,200);
        //Panel 3
        panel3.setBackground(Color.green);
        panel3.setBounds(10,220,300,200);
        //Panel 4
        panel4.setBackground(Color.cyan);
        panel4.setBounds(320,220,300,200);
        
        // Panel border
        panel.setBorder(br);
        panel2.setBorder(br);
        panel3.setBorder(br);
        panel4.setBorder(br);
        
        //adding the panel to the Container of the JFrame
        c.add(panel);
        c.add(panel2);
        c.add(panel3);
        c.add(panel4);
       
        setVisible(true);
	}


	private void initOLD() {
//		setHighlighter();
////		setPieceLabel();
//		setTabTypeButtonGroup();
//		setUpperErrorLabel();
//		setLowerErrorLabel();
//		setEncodingTextArea();
//		setTabTextArea("");
//		setRhythmSymbolsCheckBox();
//		setViewButton();
//		setMenuBar();
//		setEditorPanel();
//		//
//		setJMenuBar(getEditorMenuBar());
//		setContentPane(getEditorPanel());
////		getContentPane().add(getEditorPanel());
//		setEditorFileChooser();
//		setEditorFile(null);
//		setSize(FRAME_DIMS[0], FRAME_DIMS[1]);
//		setVisible(true);
//		setTitle(TITLE[0] + TITLE[1] + TITLE[2]);
//		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}


	private void setMenuBar() {
//		menuBar = makeMenuBar();
	}


	private JMenuBar getMenuBarThis() {
		return null; //menuBar;
	}


	private void setViewButton() {		
//		viewButton = makeJButton(
//			new Rectangle(PANEL_W/*TEXT_AREA_W*/ - BUTTON_W - MARGIN, PANEL_H, BUTTON_W, BUTTON_H), 
//			"View"
//		);
//		viewButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				viewButtonAction();
//			}
//		});
	}


	private JButton getViewButton() {
		return null; //viewButton;
	}


	private void setUpperErrorLabel() {
		JLabel l = new JLabel();
		l.setBounds(new Rectangle(99, 69, 592, 16));
		l.setForeground(Color.RED);
//		upperErrorLabel = l;
	}


	private void setLowerErrorLabel() {
		JLabel l = new JLabel();
		l.setBounds(new Rectangle(99, 88, 592, 16));
		l.setForeground(Color.RED);
//		lowerErrorLabel = l;
	}


	private void setTabTextArea(String content) {
//		tabTextArea = makeTextArea(false, content);
	}


	private JTextArea makeTextArea(boolean encoding, String content) {
		JTextArea ta = new JTextArea();
		// TODO these bounds are overridden by those of the scrollpanes in the JPanels
//		ta.setBounds(encoding ? 
//			new Rectangle(MARGIN, 105, 571, 76) : 
//			new Rectangle(MARGIN + 571 + MARGIN, 105, 571, 76));
//		ta.setBounds(encodingFrame ? new Rectangle(15, 105, 571, 76) : new Rectangle(15, 240, 571, 136));
		ta.setLineWrap(true); // necessary because of JScrollPane
		ta.setEditable(true);
		ta.setFont(FONT);
		ta.setText(content);
//		ta.setHighlighter(getHighlighter());
		ta.setHighlighter(encoding ? getHighlighter() : null); // highlighter also makes it copyable
		return ta;
	}


	private void setEditorPanel() {
//		editorPanel = makeEditorPanel();
	}


	private JPanel makeEditorPanel() {
		int PANEL_H = 200;
		JPanel p = new JPanel();
		p.setLayout(null);
		p.setBorder(BorderFactory.createTitledBorder("Encoding"));
		p.setSize(new Dimension(PANEL_DIMS[0],
//			PANEL_DIMS[1]));
			FRAME_DIMS[1]/2));

//		JLabel l = new JLabel("Piece:");
//		l.setBounds(new Rectangle(15, 15, 81, 16));
//		p.add(l, null);
//		p.add(getPieceLabel());
		//
		JLabel l = new JLabel("Style:");
		l.setBounds(new Rectangle(
			MARGIN, 
			MARGIN + PANEL_H + MARGIN, 
//			MARGIN + ENCODING_TEXT_AREA_H + MARGIN, 
			LABEL_W, 
			LABEL_H)
		);
		p.add(l, null);
//		l.setBounds(new Rectangle(PANEL_MARGIN, 559, 81, 16));
//		l.setBounds(new Rectangle(15, 15, 81, 16));
//		l.setBounds(new Rectangle(15, 42, 81, 16));
		for (AbstractButton b : Collections.list(getTabTypeButtonGroup().getElements())) {
			p.add(b, null);
		}
		//
		l = new JLabel("Error:");
		l.setBounds(new Rectangle(
			MARGIN, 
			69, 
			LABEL_W, 
			LABEL_H)
		);
		p.add(l, null);
//		p.add(getUpperErrorLabel(), null);
//		p.add(getLowerErrorLabel(), null);
		//
		p.add(getRhythmSymbolsCheckBox(), null);
		//
		p.add(getViewButton(), null);

		JScrollPane sp = 
			new JScrollPane(getEncodingTextArea(), 
			ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setBounds(new Rectangle(
			MARGIN, 
			MARGIN, 
			PANEL_W/*TEXT_AREA_W*/, 
			PANEL_H
//			ENCODING_TEXT_AREA_H
		));
//		sp.setBounds(new Rectangle(15, 115, 850, 430));
		p.add(sp, null);
		sp = 
			new JScrollPane(getTabTextArea(), 
			ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setBounds(new Rectangle(
			MARGIN, 
			MARGIN + PANEL_H /*ENCODING_TEXT_AREA_H*/ + MARGIN + BUTTON_H + MARGIN, 
			PANEL_W/*TEXT_AREA_W*/,
			PANEL_H
//			TAB_TEXT_AREA_H
		));
//		sp.setBounds(new Rectangle(15 + 15 + 650, 115, 650, 430));
		p.add(sp, null);

		return p;
	}


	private void openFileActionOLD() {
		setTextAreaContent("", getTabTextArea());
		getFileChooser().setDialogType(JFileChooser.OPEN_DIALOG);
		getFileChooser().setDialogTitle("Open");
		// Set file type filter
		getFileChooser().setFileFilter(new FileNameExtensionFilter("tab+ (.tbp)", 
			Encoding.EXTENSION.substring(1)));
		// Remove any previous selection
		getFileChooser().setSelectedFile(new File(""));
		if (getFileChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = null;
			try {
				f = getFileChooser().getSelectedFile();
				BufferedReader br = new BufferedReader(new FileReader(getFileChooser().getSelectedFile()));						
			} catch (IOException e) {
				// 11:11
				// https://www.youtube.com/watch?v=Z8p_BtqPk78
				e.printStackTrace();
			}
			setFile(f);
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
			setTextAreaContent(rawEncoding, getEncodingTextArea());
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


//	private JLabel getUpperErrorLabel() {
//		return upperErrorLabel;
//	}


//	private JLabel getLowerErrorLabel() {
//		return lowerErrorLabel;
//	}


//	private JPanel getEditorPanel() {
//		return editorPanel;
//	}

}