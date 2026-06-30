import javax.swing.*;
import java.awt.*;

public class FinalPipelineVisualizer extends JFrame {
	

    // ===== UI Components =====
    private JLabel fetchLabel, decodeLabel, executeLabel, storeLabel;
    private JLabel cycleLabel, pcLabel, r1Label;
    private JButton nextButton, autoButton, stopButton, resetButton;

    private JTable timingTable;
    private JScrollPane scrollPane;

    // ===== Simulation Variables =====
    private int cycle = 0;
    private int PC = 0;
    private int R1 = 0;

    private String[] instructions;
    private String[] pipeline = {"", "", "", ""};

    private Timer autoTimer;

    // ===== Constructor =====
    public FinalPipelineVisualizer() {

        getUserInstructions();
        buildTimingDiagram();

        setTitle("Final Pipeline Visualizer");
        setSize(950, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel(new GridLayout(2,1));

        cycleLabel = new JLabel("Cycle: 0", SwingConstants.CENTER);
        cycleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel regPanel = new JPanel();
        pcLabel = new JLabel("PC: 0");
        r1Label = new JLabel("R1: 0");

        regPanel.add(pcLabel);
        regPanel.add(r1Label);

        topPanel.add(cycleLabel);
        topPanel.add(regPanel);

        add(topPanel, BorderLayout.NORTH);

        // ===== CENTER PANEL =====
        JPanel centerPanel = new JPanel(new GridLayout(4,1,10,10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        fetchLabel = createStageLabel("FETCH");
        decodeLabel = createStageLabel("DECODE");
        executeLabel = createStageLabel("EXECUTE");
        storeLabel = createStageLabel("STORE");

        centerPanel.add(fetchLabel);
        centerPanel.add(decodeLabel);
        centerPanel.add(executeLabel);
        centerPanel.add(storeLabel);

        add(centerPanel, BorderLayout.CENTER);

        // ===== TIMING TABLE =====
        timingTable = new JTable(tableData, columnNames);
        scrollPane = new JScrollPane(timingTable);
        scrollPane.setPreferredSize(new Dimension(900, 150));
        add(scrollPane, BorderLayout.SOUTH);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel();

        nextButton = new JButton("NEXT CLOCK");
        autoButton = new JButton("AUTO RUN");
        stopButton = new JButton("STOP");
        resetButton = new JButton("RESET");

        buttonPanel.add(nextButton);
        buttonPanel.add(autoButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resetButton);

        add(buttonPanel, BorderLayout.EAST);

        nextButton.addActionListener(e -> nextCycle());
        autoButton.addActionListener(e -> autoTimer.start());
        stopButton.addActionListener(e -> autoTimer.stop());
        resetButton.addActionListener(e -> reset());

        autoTimer = new Timer(1000, e -> nextCycle());

        setVisible(true);
    }

    // ===== USER INPUT =====
    private void getUserInstructions() {

        int n = Integer.parseInt(
                JOptionPane.showInputDialog("Enter number of instructions:")
        );

        instructions = new String[n];

        for (int i = 0; i < n; i++) {
            instructions[i] = JOptionPane.showInputDialog(
                    "Enter instruction " + (i+1) + " (ADD x / SUB x):"
            ).toUpperCase();
        }
    }

    // ===== TIMING DIAGRAM =====
    private String[] columnNames;
    private String[][] tableData;

    private void buildTimingDiagram() {

        int totalCycles = instructions.length + 4 - 1;

        columnNames = new String[totalCycles + 1];
        columnNames[0] = "Instr";

        for (int i = 1; i <= totalCycles; i++) {
            columnNames[i] = "C" + i;
        }

        tableData = new String[instructions.length][totalCycles + 1];

        for (int i = 0; i < instructions.length; i++) {
            tableData[i][0] = "I" + (i+1);
        }

        for (int i = 0; i < instructions.length; i++) {
            int start = i + 1;
            tableData[i][start] = "IF";
            tableData[i][start + 1] = "ID";
            tableData[i][start + 2] = "EX";
            tableData[i][start + 3] = "WB";
        }
    }

    // ===== PIPELINE LOGIC =====
    private void nextCycle() {

        if (isPipelineEmpty() && PC >= instructions.length) {
            autoTimer.stop();
            return;
        }

        cycle++;
        cycleLabel.setText("Cycle: " + cycle);

        if (!pipeline[3].isEmpty()) {
            executeInstruction(pipeline[3]);
        }

        pipeline[3] = pipeline[2];
        pipeline[2] = pipeline[1];
        pipeline[1] = pipeline[0];

        if (PC < instructions.length) {
            pipeline[0] = instructions[PC];
            PC++;
        } else {
            pipeline[0] = "";
        }

        updateDisplay();
    }

    private void executeInstruction(String instruction) {
        String[] parts = instruction.split(" ");
        String op = parts[0];
        int value = Integer.parseInt(parts[1]);

        if (op.equals("ADD"))
            R1 += value;
        else if (op.equals("SUB"))
            R1 -= value;
    }

    private void updateDisplay() {

        fetchLabel.setText("FETCH : " + pipeline[0]);
        decodeLabel.setText("DECODE : " + pipeline[1]);
        executeLabel.setText("EXECUTE : " + pipeline[2]);
        storeLabel.setText("STORE : " + pipeline[3]);

        pcLabel.setText("PC: " + PC);
        r1Label.setText("R1: " + R1);

        highlight();
    }

    private void highlight() {
        fetchLabel.setBackground(pipeline[0].isEmpty() ? Color.LIGHT_GRAY : Color.YELLOW);
        decodeLabel.setBackground(pipeline[1].isEmpty() ? Color.LIGHT_GRAY : Color.CYAN);
        executeLabel.setBackground(pipeline[2].isEmpty() ? Color.LIGHT_GRAY : Color.ORANGE);
        storeLabel.setBackground(pipeline[3].isEmpty() ? Color.LIGHT_GRAY : Color.GREEN);
    }

    private boolean isPipelineEmpty() {
        for (String s : pipeline)
            if (!s.isEmpty())
                return false;
        return true;
    }

    private JLabel createStageLabel(String title) {
        JLabel label = new JLabel(title + " : ", SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(Color.LIGHT_GRAY);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        return label;
    }

    private void reset() {
        autoTimer.stop();
        cycle = 0;
        PC = 0;
        R1 = 0;
        pipeline = new String[]{"", "", "", ""};
        cycleLabel.setText("Cycle: 0");
        updateDisplay();
    }

    public static void main(String[] args) {
        new FinalPipelineVisualizer();
    }
}

