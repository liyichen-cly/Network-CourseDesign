package cly;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JLabel;


class FileChooser
{
	public String Chooser()
	{
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		jfc.showDialog(new JLabel(), "Ñ¡Ôñ");
		File file = jfc.getSelectedFile();
		return file.getAbsolutePath();
	}
}