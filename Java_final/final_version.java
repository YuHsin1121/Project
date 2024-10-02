import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.List;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class final_version extends JFrame {
	private StartPanel startPanel;
	private GamePanel gamePanel;
	private EndPanel endPanel;
	private Timer gameTimer;
	private ArrayList<Note> notes = new ArrayList<>();
	private List<Integer> noteTimes = new ArrayList<>();
	private List<Integer> gifTimes = new ArrayList<>();
	private long startTime;
	private Clip audioClip = null;
	private int combo = 0;
	private int highestCombo = 0;
	private int score = 0;
	// 0: perfect, 1: great, 2: good, 3: bad, 4: miss
	private int[] combos = new int[5];
	private JLabel label;
	private JLabel gifLabel;
	
	private class StartPanel extends JPanel{
		private BufferedImage background;
		private String selectedSong;
		private String selectedNotes;
		private String selectedSkillNotes;
		private String selectedNotesImage;
		private boolean writeMode = false;
		
		public StartPanel(String backgroundPath){
			try {
				background = ImageIO.read(new File(backgroundPath));
			} catch (IOException e){
				e.printStackTrace();
			}
			
			setLayout(null);
			
			addSongButton(
				"C:\\Users\\User\\Downloads\\java_project_final\\parade.wav",
                "C:\\Users\\User\\Downloads\\java_project_final\\note01.txt", 
                "C:\\Users\\User\\Downloads\\java_project_final\\background01.png",
				"C:\\Users\\User\\Downloads\\java_project_final\\skillnote01.txt", 
				"C:\\Users\\User\\Downloads\\java_project_final\\button01.jpg",
				"C:\\Users\\User\\Downloads\\java_project_final\\notes01.png", 120);
            addSongButton(
				"C:\\Users\\User\\Downloads\\java_project_final\\levanpolkka.wav",
                "C:\\Users\\User\\Downloads\\java_project_final\\note02.txt", 
                "C:\\Users\\User\\Downloads\\java_project_final\\background02.jpg", 
				"C:\\Users\\User\\Downloads\\java_project_final\\skillnote02.txt",
				"C:\\Users\\User\\Downloads\\java_project_final\\button02.jpg", 
				"C:\\Users\\User\\Downloads\\java_project_final\\notes02.png", 185);
            addSongButton(
				"C:\\Users\\User\\Downloads\\java_project_final\\flyhigh.wav",
                "C:\\Users\\User\\Downloads\\java_project_final\\note03.txt", 
                "C:\\Users\\User\\Downloads\\java_project_final\\background03.jpg",
				"C:\\Users\\User\\Downloads\\java_project_final\\skillnote03.txt",
				"C:\\Users\\User\\Downloads\\java_project_final\\button03.jpg",
				"C:\\Users\\User\\Downloads\\java_project_final\\notes03.png", 250);
			addSongButton(
				"C:\\Users\\User\\Downloads\\java_project_final\\zero.wav",
                "C:\\Users\\User\\Downloads\\java_project_final\\note04.txt", 
                "C:\\Users\\User\\Downloads\\java_project_final\\background04.jpg",
				"C:\\Users\\User\\Downloads\\java_project_final\\skillnote04.txt",
				"C:\\Users\\User\\Downloads\\java_project_final\\button04.jpg",
				"C:\\Users\\User\\Downloads\\java_project_final\\notes04.png", 315);
				
			JButton writingMode = new JButton();
			ImageIcon buttonIcon = new ImageIcon(new ImageIcon("C:\\Users\\User\\Downloads\\java_project_final\\gameMode.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
			writingMode.setIcon(buttonIcon);
			writingMode.setBounds(373, 345, 60, 60);
			writingMode.setBorderPainted(false);
			writingMode.setContentAreaFilled(false);
			writingMode.setFocusPainted(false);
			writingMode.setOpaque(false);
			writingMode.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
					writeMode = !writeMode;
					if (writeMode) {
						writingMode.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\User\\Downloads\\java_project_final\\writingMode.png").getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
					} else {
						writingMode.setIcon(buttonIcon);
					}
                }
            });
			add(writingMode);
		}
		
		private void addSongButton(String songPath, String notePath, String backgroundPath, String skillNotesPath, String buttonPath, String notesImagePath, int yPosition){
			JButton songButton = new JButton();
            songButton.setBounds(110, yPosition, 230, 65);
			songButton.setBorderPainted(false);
			songButton.setContentAreaFilled(false);
			songButton.setFocusPainted(false);
			songButton.setOpaque(false);
            songButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectedSong = songPath;
                    selectedNotes = notePath;
					selectedSkillNotes = skillNotesPath;
					selectedNotesImage = notesImagePath;
                    gamePanel.setBackgroundPath(backgroundPath);
					gamePanel.setNoteImagePath(selectedNotesImage);
					startGame();
					/*
					if (writeMode) {
						long currentTime = System.currentTimeMillis(); 
						long noteTime = currentTime - startTime; 
						writeNotesToFile(noteTime);
					}*/
                }
            });
			
			ImageIcon buttonIcon = new ImageIcon(new ImageIcon(buttonPath).getImage().getScaledInstance(230, 65, Image.SCALE_SMOOTH));
			songButton.setIcon(buttonIcon);
            add(songButton);
		}
		
		@Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);

            if (background != null)
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
		}
	}
	
	private class GamePanel extends JPanel {
		private BufferedImage backgroundImage;
		private BufferedImage noteImage;
		private JButton skip;
		private String backgroundPath;
		private String noteImagePath;

		public GamePanel() {
			setLayout(null);

			label = new JLabel();
			label.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
			label.setForeground(new Color(255, 205, 63));
			label.setBounds(200, 30, 400, 50);
			add(label);

			skip = new JButton();
			skip.setBounds(380, 15, 50, 50);
			ImageIcon buttonIcon = new ImageIcon(new ImageIcon("C:\\Users\\User\\Downloads\\java_project_final\\skip.png").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
			skip.setIcon(buttonIcon);
			skip.setBorderPainted(false);
			skip.setContentAreaFilled(false);
			skip.setFocusPainted(false);
			skip.setOpaque(false);
			skip.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					audioClip.stop();
					gameTimer.stop();
					showGameOverScreen();
				}
			});
			add(skip);

			gifLabel = new JLabel();
			gifLabel.setBounds(0, 0, 460, 460);
			add(gifLabel);
		}

		public void setBackgroundPath(String path) {
			this.backgroundPath = path;
			try {
				backgroundImage = ImageIO.read(new File(path));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void setNoteImagePath(String path) {
			this.noteImagePath = path;
			try {
				noteImage = ImageIO.read(new File(path));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (backgroundImage != null) {
				g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			}

			g.setColor(Color.WHITE);
			g.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
			g.drawString(String.valueOf(combo), 355, 100);
			g.setColor(new Color(240, 208, 117));
			g.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
			g.drawString("combo", 340, 120);

			g.setColor(new Color(226, 218, 193));
			g.fillRect(getWidth() / 2 - 25, 350, 50, 10);
	
			for (Note note : final_version.this.notes) {
				if (noteImage != null) {
					g.drawImage(noteImage, getWidth() / 2 - 25, note.getY(), 50, 50, this);
				} else {
					g.setColor(Color.RED);
					g.fillOval(getWidth() / 2 - 25, note.getY(), 50, 50);
				}
			}
		}
	}
	
	private class EndPanel extends JPanel{
        private BufferedImage background;
		
		public EndPanel(String backgroundPath){
			try {
				background = ImageIO.read(new File(backgroundPath));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			setLayout(null);
			JLabel title = new JLabel("Congratulations!", SwingConstants.CENTER);
			title.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
			title.setForeground(new Color(240, 208, 117));
			title.setBounds(20, 30, 400, 60);
			add(title);
			
			JLabel comboLabel = new JLabel("Combo: " + highestCombo, SwingConstants.CENTER);
			comboLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
			comboLabel.setForeground(Color.WHITE);
			comboLabel.setBounds(80, 90, 300, 50);
			add(comboLabel);
			
			JLabel scoreLabel = new JLabel("Score: " + score, SwingConstants.CENTER);
			scoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
			scoreLabel.setForeground(Color.WHITE);
			scoreLabel.setBounds(80, 120, 300, 50);
			add(scoreLabel);
			
			addEndStatsLabel("Perfect: ", combos[0], 180);
            addEndStatsLabel("Great: ", combos[1], 210);
            addEndStatsLabel("Good: ", combos[2], 240);
            addEndStatsLabel("Bad: ", combos[3], 270);
            addEndStatsLabel("Miss: ", combos[4], 300);
			
			JButton retryButton = new JButton("Retry");
			retryButton.setBounds(140, 340, 80, 30);
            retryButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    restartGame();
                }
            });
            add(retryButton);

            JButton exitButton = new JButton("Exit");
			exitButton.setBounds(240, 340, 80, 30);
            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            add(exitButton);
		}
		
		private void addEndStatsLabel(String text, int value, int yPosition) {
            JLabel label = new JLabel(text + value, SwingConstants.LEFT);
            label.setFont(new Font("Comic Sans MS", Font.BOLD, 15));
            label.setForeground(Color.WHITE);
            label.setBounds(190, yPosition, 300, 30);
            add(label);
        }
		
		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			
			if (background != null)
				g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
		}
	}
	
	private class Note {
        private int y = 0;
        private final int speed = 5;

        public void move() {
            y += speed;
        }

        public int getY() {
            return y;
        }
		
		public boolean isPerfect() {
			return y >= 340 && y <= 360;
		}

        public boolean isGreat() {
			return y >= 330 && y <= 370;
		}
		
		public boolean isGood() {
			return y >= 320 && y <= 380;
		}
		
		public boolean isBad() {
			return y >= 310 && y <= 390;
		}

        public boolean isMissed() {
            return y > getHeight();
        }
    }
	
	public final_version() {
        setTitle("Rhythm Game");
        setSize(460, 460);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        String startBackgroundImagePath = "C:\\Users\\User\\Downloads\\java_project_final\\start.png";
        String endBackgroundImagePath = "C:\\Users\\User\\Downloads\\java_project_final\\end01.jpg";
		
		startPanel = new StartPanel(startBackgroundImagePath);
        gamePanel = new GamePanel();
        endPanel = new EndPanel(endBackgroundImagePath);
		
		add(startPanel);

        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    checkHit();
                }
            }
        });

        gameTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGame();
                gamePanel.repaint();
				generateNotes();
				checkGifTimes();
            }
        });
    }
	
	private void clearNoteFile() {
		String filePath = "newNote.txt";
		try {
			FileWriter writer = new FileWriter(filePath, false); 
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    private void writeNotesToFile(long noteTime) {
		if (!startPanel.writeMode) return;
		String filePath = "newNotes.txt";
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(filePath, true));
			writer.write(String.valueOf(noteTime));
			writer.newLine();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void playMusic(String path) {
        try {
            File audioFile = new File(path);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
			audioClip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        gameTimer.stop();
                        showGameOverScreen();
                    }
                }
            });
            audioClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
	
	private void startGame() {
		clearNoteFile();
		getContentPane().removeAll();
		add(gamePanel);
		revalidate();
		repaint();
		combo = 0;
		notes.clear();
		highestCombo = 0;
		noteTimes.clear();
		gifTimes.clear();

		playMusic(startPanel.selectedSong);
		generateNotesFromFile(startPanel.selectedNotes);
		generateGifTimesFromFile(startPanel.selectedSkillNotes);
		startTime = System.currentTimeMillis();
		gameTimer.start();
	}

	
	private void generateNotesFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                noteTimes.add(Integer.parseInt(line.trim()) - 700);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	private void generateNotes() {
        long currentTime = System.currentTimeMillis();
        Iterator<Integer> iterator = noteTimes.iterator();
        while (iterator.hasNext()) {
            int noteTime = iterator.next();
            if (currentTime - startTime >= noteTime) {
                notes.add(new Note()); 
                iterator.remove();
            }
        }
    }
	
	private void generateGifTimesFromFile(String filePath) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				gifTimes.add(Integer.parseInt(line.trim()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkGifTimes() {
		long currentTime = System.currentTimeMillis();
		Iterator<Integer> iterator = gifTimes.iterator();
		while (iterator.hasNext()) {
			int gifTime = iterator.next();
			if (currentTime - startTime >= gifTime) {
				playGIF("C:\\Users\\User\\Downloads\\java_project_final\\rainbowcat.gif");
				iterator.remove();
			}
		}
	}
	
	private void updateGame() {
        Iterator<Note> iterator = notes.iterator();
        while (iterator.hasNext()) {
            Note note = iterator.next();
            note.move();
            if (note.isMissed()) {
				if (combo > highestCombo) {
					highestCombo = combo;
				}
				combos[4]++;
				combo = 0;
				label.setText("Miss");
                iterator.remove();
            }
        }
    }
	
	private void checkHit() {
        Iterator<Note> iterator = notes.iterator();
        while (iterator.hasNext()) {
            Note note = iterator.next();
            if (note.isPerfect()) {
				if (combo > highestCombo) {
					highestCombo = combo;
				}
				combos[0]++;
				combo++;
				score += 1000;
				label.setText("Perfect");
				iterator.remove();
				break;
			}
			if (note.isGreat()) {
				if (combo > highestCombo) {
					highestCombo = combo;
				}
				combos[1]++;
				combo++;
				score += 750;
				label.setText("Great");
				iterator.remove();
				break;
			}
			if (note.isGood()) {
				if (combo > highestCombo) {
					highestCombo = combo;
				}
				combos[2]++;
				combo++;
				score += 500;
				label.setText("Good");
				iterator.remove();
				break;
			}
			if (note.isBad()) {
				if (combo > highestCombo) {
					highestCombo = combo;
				}
				combos[3]++;
				combo = 0;
				score += 250;
				label.setText("Bad");
				iterator.remove();
				break;
			}
        }
    }
	
	private void showGameOverScreen() {
		String endBG01 = "C:\\Users\\User\\Downloads\\java_project_final\\end01.jpg";
        String endBG02 = "C:\\Users\\User\\Downloads\\java_project_final\\end02.jpg";
        String endBG03 = "C:\\Users\\User\\Downloads\\java_project_final\\end03.jpg";
		getContentPane().removeAll();
		if (highestCombo < 10)
			endPanel = new EndPanel(endBG02);
		else if (highestCombo >= 10 && highestCombo < 30)
			endPanel = new EndPanel(endBG03);
		else endPanel = new EndPanel(endBG01);
        add(endPanel);
        revalidate();
        repaint();
    }

    private void restartGame() {
        getContentPane().removeAll();
        add(startPanel);
        revalidate();
        repaint();
    }
	
	private void playGIF(String gifPath) {
		ImageIcon gifIcon = new ImageIcon(gifPath);
		gifLabel.setIcon(gifIcon);
		gifLabel.setVisible(true);
		gifLabel.repaint();
		Timer timer = new Timer(1400, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gifLabel.setIcon(null);
				gifLabel.setVisible(false);
			}
		});
		timer.setRepeats(false);
		timer.start();
	}

	
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new final_version().setVisible(true);
            }
        });
	}
}