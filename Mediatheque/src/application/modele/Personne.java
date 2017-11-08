package application.modele;

import java.util.ArrayList;

public class Personne {
	private String strNoPersonne;
	private String strNom;
	private String strPrenom;
	private String strAdresse;
	private String strNoTelephone;
	private ArrayList<Pret> lstPrets=new ArrayList<Pret>();
	public Personne(String strNom, String strPrenom, String strAdresse, String strNoTelephone,String strNoPersonne) {
		this.strNom = strNom;
		this.strPrenom = strPrenom;
		this.strAdresse = strAdresse;
		this.strNoTelephone = strNoTelephone;
		
	}

	public String getStrNoPersonne() {
		return strNoPersonne;
	}

	public ArrayList<Pret> getLstPrets() {
		return lstPrets;
	}

	public void setLstPrets(ArrayList<Pret> lstPrets) {
		this.lstPrets = lstPrets;
	}

	public String getStrNom() {
		return strNom;
	}

	public String getStrPrenom() {
		return strPrenom;
	}

}
