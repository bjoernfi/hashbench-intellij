package hashbench.benchmark;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import hashbench.util.LogMessage;
import org.jetbrains.annotations.Nullable;

public class ResultDialog extends DialogWrapper {

    private static final Logger LOG = Logger.getInstance(ResultDialog.class);

    private JPanel contentPanel;
    private JTextArea checksumTextArea;
    private JTextArea mailTextArea;

    public ResultDialog(String jsonResult) {
        super(true);

        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel labelComment = new JBLabel("Output:");
        var gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        contentPanel.add(labelComment, gridBagConstraints);

        checksumTextArea = new JBTextArea(jsonResult);
        checksumTextArea.setRows(7);
        checksumTextArea.setWrapStyleWord(true);
        checksumTextArea.setLineWrap(true);
        checksumTextArea.setEditable(false);
        JScrollPane pseudonymsScrollPane = ScrollPaneFactory.createScrollPane(checksumTextArea);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = JBUI.insetsTop(5);
        contentPanel.add(pseudonymsScrollPane, gridBagConstraints);

        var infoLabel = new JBLabel(
                "Please copy this output and send it to the following mail address:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = JBUI.insetsTop(10);
        contentPanel.add(infoLabel, gridBagConstraints);

        mailTextArea = new JBTextArea("changeme@demo.local");
        mailTextArea.setRows(1);
        mailTextArea.setWrapStyleWord(true);
        mailTextArea.setLineWrap(true);
        mailTextArea.setEditable(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = JBUI.insetsTop(5);
        contentPanel.add(mailTextArea, gridBagConstraints);

        setTitle("Result");
        setOKButtonText("Copy");
        setCancelButtonText("Close");
        setResizable(true);
        init();
    }

    @Override
    protected void doOKAction() {
        try {
            StringSelection selection = new StringSelection(checksumTextArea.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            setOKButtonText(" Copied ");
        } catch (Exception e) {
            LOG.error(LogMessage.from("Failed to copy result to clipboard"), e);
        }
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return checksumTextArea;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPanel;
    }

}
