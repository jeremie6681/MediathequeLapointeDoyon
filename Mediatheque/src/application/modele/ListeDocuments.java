package application.modele;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import application.controleur.GestionDocuments;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @Description R�cup�re les donn�es des documents en m�moire par lecture ou
 *              s�rialisation et d�s�rialisation. Les donn�es sont aussi
 *              entrepos� dans la classe.
 * 
 * @Implements Serializable
 * 
 * @author Lapointe & Doyon
 *
 */
public final class ListeDocuments implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7935983340493130610L;
	private final String strNomFichierDVD = "Donnee/DVD.txt";
	private final String strNomFichierLivre = "Donnee/Livres.txt";
	private final String strNomFichierPeriodique = "Donnee/Periodiques.txt";
	private final String strNomFichierSerialiser = "Donnee/lstDocuments.ser";

	//Liste contenant les documents s�par� par type
	private ObservableList<Document> lstLivre = FXCollections.observableArrayList();
	private ObservableList<Document> lstDvd = FXCollections.observableArrayList();
	private ObservableList<Document> lstPeriodique = FXCollections.observableArrayList();

	public Map<TypeDocument, ObservableList<Document>> mapDocument = new HashMap<>();

	private static ListeDocuments instanceDoc;

	private ListeDocuments() {
		mapDocument.put(TypeDocument.Livre, lstLivre);
		mapDocument.put(TypeDocument.Periodique, lstPeriodique);
		mapDocument.put(TypeDocument.Dvd, lstDvd);

		File fichierSerialiser = new File(strNomFichierSerialiser);

		// S'il y a eu d�j� une s�rialisation
		if (fichierSerialiser.exists()) {
			deserialisation();
		} else {
			lectureFichierOriginal();
		}
	}

	private void lectureFichierOriginal() {
		// Lecture fichier (2 = dvd, 0 = livre, 1 = periodique)
		for (int intTypeDocument = 0; intTypeDocument < 3; intTypeDocument++) {
			BufferedReader brFichier = null;
			try {
				if (intTypeDocument == 2) {
					brFichier = new BufferedReader(new FileReader(strNomFichierDVD));
				} else if (intTypeDocument == 0) {
					brFichier = new BufferedReader(new FileReader(strNomFichierLivre));
				} else {
					brFichier = new BufferedReader(new FileReader(strNomFichierPeriodique));
				}

			} catch (FileNotFoundException e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			String strLigne;

			try {
				while ((strLigne = brFichier.readLine()) != null) {
					String[] tabLigne = strLigne.split(",");

					LocalDate dateDocument = LocalDate.parse(tabLigne[2].trim(),
							DateTimeFormatter.ofPattern("dd-MM-uuuu"));

					// Cr�e le document et l'ajoute dans la liste selon son type
					if (intTypeDocument == 2) {
						DVD objDVD = new DVD(tabLigne[0].trim(), tabLigne[1].trim(), dateDocument, Etat.DISPONIBLE,
								Short.parseShort(tabLigne[3].trim()), tabLigne[4].trim());
						lstDvd.add(objDVD);
					} else if (intTypeDocument == 0) {
						Livre objLivre = new Livre(tabLigne[0].trim(), tabLigne[1].trim(), dateDocument,
								Etat.DISPONIBLE, tabLigne[3].trim());
						lstLivre.add(objLivre);
					} else {
						Periodique objPeriodique = new Periodique(tabLigne[0].trim(), tabLigne[1].trim(), dateDocument,
								Etat.DISPONIBLE, Integer.parseInt(tabLigne[3].trim()),
								Integer.parseInt(tabLigne[4].trim()));
						lstPeriodique.add(objPeriodique);
					}
				}

				// Parcours la liste de documents pour ajouter des mots cl�
				mapDocument.get(TypeDocument.values()[intTypeDocument]).forEach(f -> GestionDocuments.motCleAjout(f));
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				System.out.println(e);
			}
		}
	}

	public void serialisation() {
		try {
			FileOutputStream fichier = new FileOutputStream(strNomFichierSerialiser);
			ObjectOutputStream oos = new ObjectOutputStream(fichier);

			Map<TypeDocument, List<Document>> maptempo = new HashMap<TypeDocument, List<Document>>();

			// Passage d'observableList � des arrayList
			maptempo.put(TypeDocument.Livre, new ArrayList<>(mapDocument.get(TypeDocument.Livre)));
			maptempo.put(TypeDocument.Periodique, new ArrayList<>(mapDocument.get(TypeDocument.Periodique)));
			maptempo.put(TypeDocument.Dvd, new ArrayList<>(mapDocument.get(TypeDocument.Dvd)));

			oos.writeObject(maptempo);
			oos.close();
			fichier.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void deserialisation() {

		try {
			FileInputStream fichier = new FileInputStream(strNomFichierSerialiser);
			ObjectInputStream ois = new ObjectInputStream(fichier);

			Map<TypeDocument, List<Document>> mapTempo = (HashMap<TypeDocument, List<Document>>) ois.readObject();

			// Passage d'arrayList � des onservableList
			mapDocument.get(TypeDocument.Livre).addAll(mapTempo.get(TypeDocument.Livre));
			this.lstLivre = mapDocument.get(TypeDocument.Livre);

			mapDocument.get(TypeDocument.Periodique).addAll(mapTempo.get(TypeDocument.Periodique));
			this.lstPeriodique = mapDocument.get(TypeDocument.Periodique);

			mapDocument.get(TypeDocument.Dvd).addAll(mapTempo.get(TypeDocument.Dvd));
			this.lstDvd = mapDocument.get(TypeDocument.Dvd);

			ois.close();
			fichier.close();
		} catch (IOException e) {
			System.err.println("erreur avec la lecture des ficher de s�rialisation des documents");
			e.printStackTrace();
		} catch (ClassNotFoundException e2) {
			System.err.println("erreur avec la d�s�rialisation des documents");
		}
	}
	
	//S'assure que la classe n'est qu'une seul et unique instance
	public static ListeDocuments getInstance() {
		if (instanceDoc == null) {
			instanceDoc = new ListeDocuments();
		}
		return instanceDoc;
	}
}
