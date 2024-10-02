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


public class test09 extends JFrame {
	private StartPanel startPanel;
	private GamePanel gamePanel;
	private EndPanel endPanel;
	private Timer gameTimer;
	private ArrayList<Note> notes = new ArrayList<>();
	private List<Integer> noteTimes = new ArrayList<>();
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
		
		public StartPanel(String backgroundPath){
			try {
				background = ImageIO.read(new File(backgroundPath));
			} catch (IOException e){
				e.printStackTrace();
			}
			
			setLayout(null);
			
			JButton startButton = new JButton("Start");
			startButton.setBounds(180, 340, 80, 30);
            startButton.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    startGame();
                }
            });

            add(startButton);
		}
		
		@Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);

            if (background != null)
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
		}
	}
	
	private class GamePanel extends JPanel{
        private BufferedImage backgroundImage;
        private BufferedImage noteImage;
		private JButton skip;

        public GamePanel(String backgroundPath, String notePath){
            try{
                backgroundImage = ImageIO.read(new File(backgroundPath));
                noteImage = ImageIO.read(new File(notePath));
            } catch (IOException e){
                e.printStackTrace();
            }
			
			setLayout(null);
			
			label = new JLabel();
			label.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
			label.setForeground(new Color(255, 205, 63));
			label.setBounds(200, 30, 400, 50);
			add(label);
			
			skip = new JButton("Skip");
			skip.setBounds(360, 20, 80, 30);
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

        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);

            if (backgroundImage != null){
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

            for (Note note : test09.this.notes){ 
                if (noteImage != null){
                    g.drawImage(noteImage, getWidth() / 2 - 25, note.getY(), 50, 50, this);
                }
				else{
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
			
			JLabel perfectLabel = new JLabel("Perfect: " + combos[0], SwingConstants.LEFT);
			perfectLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 15));
			perfectLabel.setForeground(Color.WHITE);
			perfectLabel.setBounds(190, 180, 300, 30);
			add(perfectLabel);

			JLabel greatLabel = new JLabel("Great:   " + combos[1], SwingConstants.LEFT);
			greatLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 15));
			greatLabel.setForeground(Color.WHITE);
			greatLabel.setBounds(190, 210, 300, 30);
			add(greatLabel);

			JLabel goodLabel = new JLabel("Good:    " + combos[2], SwingConstants.LEFT);
			goodLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 15));
			goodLabel.setForeground(Color.WHITE);
			goodLabel.setBounds(190, 240, 300, 30);
			add(goodLabel);

			JLabel badLabel = new JLabel("Bad:     " + combos[3], SwingConstants.LEFT);
			badLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 15));
			badLabel.setForeground(Color.WHITE);
			badLabel.setBounds(190, 270, 300, 30);
			add(badLabel);

			JLabel missLabel = new JLabel("Miss:    " + combos[4], SwingConstants.LEFT);
			missLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 15));
			missLabel.setForeground(Color.WHITE);
			missLabel.setBounds(190, 300, 300, 30);
			add(missLabel);
			
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
	
	public test09() {
        setTitle("Game");
        setSize(460, 460);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        String startBackgroundImagePath = "C:\\Users\\melod\\Downloads\\java_project_final\\start.png";
        String endBackgroundImagePath = "C:\\Users\\melod\\Downloads\\java_project_final\\end01.jpg";
        String gameBackgroundImagePath = "C:\\Users\\melod\\Downloads\\java_project_final\\background02.jpg";
        String noteImagePath = "C:\\Users\\melod\\Downloads\\java_project_final\\notes02.png";
		
		startPanel = new StartPanel(startBackgroundImagePath);
        gamePanel = new GamePanel(gameBackgroundImagePath, noteImagePath);
        endPanel = new EndPanel(endBackgroundImagePath);
		
		add(startPanel);

        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {				
            checkHit();
            long currentTime = System.currentTimeMillis(); 
            long noteTime = currentTime - startTime; 
            writeNoteTimeToFile(noteTime); 
        }			
    }
        });

        gameTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGame();
                gamePanel.repaint();
				generateNotes();
            }
        });
		
    }
	
	
private void clearNoteFile() {
    try {
		String filePath = "gnote.txt";
      
        FileWriter writer = new FileWriter(filePath, false); 

        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        bufferedWriter.close();

        System.out.println("Note file has been cleared successfully.");
    } catch (IOException e) {
        e.printStackTrace();
}
}
	
	private void writeNoteTimeToFile(long noteTime) {
	try {
    String filePath = "gnote.txt";

   // FileWriter writer = new FileWriter(filePath, false);
    FileWriter writer = new FileWriter(filePath, true);

    BufferedWriter bufferedWriter = new BufferedWriter(writer);
	long tempnoteTime=noteTime-600;
    //bufferedWriter.write(String.valueOf(noteTime));
	bufferedWriter.write(String.valueOf(tempnoteTime));
    bufferedWriter.newLine();

    bufferedWriter.close();

  //  System.out.println("Note time has been written to file successfully.");
} catch (IOException e) {
    e.printStackTrace();
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
		noteTimes.clear();
        highestCombo = 0;

        String filePath = "C:\\Users\\melod\\Downloads\\java_project_final\\levanpolkka.wav";
        playMusic(filePath);
        //generateNotesFromFile("C:\\Users\\melod\\Downloads\\java_project_final\\note02.txt");
        startTime = System.currentTimeMillis(); 
		gameTimer.start();
    }
	
	private void generateNotesFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                noteTimes.add(Integer.parseInt(line.trim()) - 700);//Integer.parseInt(line.trim()) - 700
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
				playGIF("C:\\Users\\melod\\Downloads\\java_project_final\\rainbowcat.gif");
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
		String endBG01 = "C:\\Users\\melod\\Downloads\\java_project_final\\end01.jpg";
        String endBG02 = "C:\\Users\\melod\\Downloads\\java_project_final\\end02.jpg";
        String endBG03 = "C:\\Users\\melod\\Downloads\\java_project_final\\end03.jpg";
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
		Timer timer = new Timer(3000, new ActionListener() {
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
                new test09().setVisible(true);
            }
        });
	}
}