package ba.unsa.etf.rpr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GeografijaDAO {
    private static GeografijaDAO instance;
    private static Connection connection;

    private PreparedStatement glavniGradUpit, dajDrzavuUpit, obrisiDrzavuUpit, obrisiSveGradoveUDrzavi, obrisiGradUpit,
            izmijeniDrzavuUpit, dajIdDrzaveUpit, dajSveGradoveUpit, dodajGradUpit, dodajDrzavuUpit, izmijeniGradUpit,
            dajDostupanIdZaGrad, dajSveDrzaveUpit, dajDostupanIdZaDrzavu;


    public static GeografijaDAO getInstance() {
        if(instance == null) {
            instance = new GeografijaDAO();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private GeografijaDAO() {
        try {
            //u slučaju da baza ne postoji sljedeća konstrukcija će kreirati praznu bazu tako da ovdje neće doći do bacanja izuzetka
            connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Amila\\IdeaProjects\\tutorijal-11-azigo12\\baza.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            glavniGradUpit = connection.prepareStatement("SELECT grad.id, grad.naziv, grad.broj_stanovnika, grad.drzava, grad.slika, grad.postanski_broj FROM grad, drzava WHERE grad.drzava=drzava.id AND drzava.naziv=?");
        } catch (SQLException e) {
            regenerisiBazu();
            try {
                glavniGradUpit = connection.prepareStatement("SELECT grad.id, grad.naziv, grad.broj_stanovnika, grad.drzava, grad.slika, grad.postanski_broj FROM grad, drzava WHERE grad.drzava=drzava.id AND drzava.naziv=?");
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }


        try {
            dajDrzavuUpit = connection.prepareStatement("SELECT * FROM drzava WHERE id = ?");
            obrisiDrzavuUpit = connection.prepareStatement("DELETE FROM drzava WHERE naziv = ?");
            obrisiSveGradoveUDrzavi = connection.prepareStatement("DELETE FROM grad WHERE drzava = ?");
            dajIdDrzaveUpit = connection.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
            dajSveGradoveUpit = connection.prepareStatement("SELECT * FROM grad");
            dodajGradUpit = connection.prepareStatement("INSERT INTO grad (id, naziv, broj_stanovnika, drzava, slika, postanski_broj) VALUES (?,?,?,?,?,?)");
            dodajDrzavuUpit = connection.prepareStatement("INSERT INTO drzava (id, naziv, glavni_grad) VALUES (?,?,?)");
            izmijeniGradUpit = connection.prepareStatement("UPDATE grad SET naziv = ?, broj_stanovnika = ?, drzava = ?, slika = ?, postanski_broj = ? WHERE id = ?");
            dajDostupanIdZaGrad = connection.prepareStatement("SELECT MAX(id) FROM grad");
            dajSveDrzaveUpit = connection.prepareStatement("SELECT * FROM drzava");
            dajDostupanIdZaDrzavu = connection.prepareStatement("SELECT MAX(id) FROM drzava");
            obrisiGradUpit = connection.prepareStatement("DELETE FROM grad WHERE naziv = ?");
            izmijeniDrzavuUpit = connection.prepareStatement("UPDATE drzava SET naziv = ?, glavni_grad = ? WHERE id = ?");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void removeInstance(){
        if(instance == null) return;
        instance.close();
        instance = null;
    }
    public void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void regenerisiBazu() {
        Scanner ulaz = null;
        try {
            ulaz = new Scanner(new FileInputStream("baza.db.sql"));
            String sqlUpit = "";
            while (ulaz.hasNext()) {
                sqlUpit += ulaz.nextLine();
                if ( sqlUpit.charAt( sqlUpit.length()-1 ) == ';') {
                    try {
                        Statement stmt = connection.createStatement();
                        stmt.execute(sqlUpit);
                        sqlUpit = "";
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            ulaz.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Grad glavniGrad(String drzava) {
        try {
            glavniGradUpit.setString(1, drzava);
            ResultSet rs = glavniGradUpit.executeQuery();
            if(!rs.next()) return null;
            return dajGradIzResultSeta(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Grad dajGradIzResultSeta(ResultSet rs) throws SQLException {
        Grad g = new Grad(rs.getInt(1), rs.getString(2), rs.getInt(3), null, rs.getString(5), rs.getInt(6));
        g.setDrzava(dajDrzavu(rs.getInt(4), g));
        return g;
    }

    private Drzava dajDrzavu(int id, Grad g) {
        try {
            dajDrzavuUpit.setInt(1,id);
            ResultSet rs = dajDrzavuUpit.executeQuery();
            if(!rs.next()) return null;
            return dajDrzavuIzResultSeta(rs, g);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Drzava dajDrzavuIzResultSeta(ResultSet rs, Grad g) throws SQLException {
        return new Drzava(rs.getInt(1), rs.getString(2), g);
    }

    public void obrisiDrzavu(String drzava) {
        try {
            dajIdDrzaveUpit.setString(1, drzava);
            ResultSet rs = dajIdDrzaveUpit.executeQuery();
            int idDrzave = 0;
            if(!rs.next()) {
                return;
            }
            else {
                idDrzave = rs.getInt(1);
                obrisiDrzavuUpit.setString(1,drzava);
                obrisiDrzavuUpit.executeUpdate();
                obrisiSveGradoveUDrzavi.setInt(1, idDrzave);
                obrisiSveGradoveUDrzavi.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Grad> gradovi() {
        ArrayList<Grad> rezultat = new ArrayList<>();

        try {
            ResultSet rs = dajSveGradoveUpit.executeQuery();
            //ovdje sam koristila rs.next() ali mi je preskakalo prvi grad uvijek jer next() provjeri ima li nesto na pocetku i onda se pomjeri jedno mjesto unaprijed
            if(!rs.isBeforeFirst()) return null;
            while(rs.next()) {
                Grad g = dajGradIzResultSeta(rs);
                if(g != null) rezultat.add(g);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Collections.sort(rezultat);
        return rezultat;
    }

    public void dodajGrad(Grad grad) {
        try {
            ResultSet rs = dajDostupanIdZaGrad.executeQuery();
            int id = 1;
            id = rs.getInt(1) + 1;
            grad.setId(id);

            dodajGradUpit.setInt(1, id);
            dodajGradUpit.setString(2, grad.getNaziv());
            dodajGradUpit.setInt(3,grad.getBrojStanovnika());
            if(grad.getDrzava() == null)
                dodajGradUpit.setInt(4,-1);
            else dodajGradUpit.setInt(4, grad.getDrzava().getId());
            dodajGradUpit.setString(5, grad.getSlika());
            dodajGradUpit.setInt(6, grad.getPostanskiBroj());
            dodajGradUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void izmijeniDrzavu(Drzava d, int id) {
        try {
            izmijeniDrzavuUpit.setString(1, d.getNaziv());
            izmijeniDrzavuUpit.setInt(2, id);
            izmijeniDrzavuUpit.setInt(3, d.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void dodajDrzavu(Drzava drzava) {
        try {
            ResultSet rs = dajDostupanIdZaDrzavu.executeQuery();
            int idDrzave = 1;
            if(rs.next()) idDrzave = rs.getInt(1) + 1;
            drzava.setId(idDrzave);

            dodajDrzavuUpit.setInt(1, idDrzave);
            dodajDrzavuUpit.setString(2, drzava.getNaziv());
            if(drzava.getGlavniGrad() != null)
                dodajDrzavuUpit.setInt(3, drzava.getGlavniGrad().getId());
            else
                dodajDrzavuUpit.setInt(3, -1);
            dodajDrzavuUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void izmijeniGrad(Grad grad) {
        try {
            izmijeniGradUpit.setString(1, grad.getNaziv());
            izmijeniGradUpit.setInt(2, grad.getBrojStanovnika());
            izmijeniGradUpit.setInt(3, grad.getDrzava().getId());
            izmijeniGradUpit.setString(4,grad.getSlika());
            izmijeniGradUpit.setInt(5, grad.getPostanskiBroj());
            izmijeniGradUpit.setInt(6, grad.getId());
            izmijeniGradUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Drzava nadjiDrzavu(String drzava) {
        try {
            dajIdDrzaveUpit.setString(1, drzava);
            ResultSet rs = dajIdDrzaveUpit.executeQuery();
            if(!rs.next()) return null;
            int idDrzave = rs.getInt(1);
            Grad g = glavniGrad(drzava);
            return new Drzava(idDrzave, drzava, g);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Drzava> dajSveDrzave() {
        ArrayList<Drzava> rezultat = new ArrayList<>();
        try {
            ResultSet rs = dajSveDrzaveUpit.executeQuery();
            if(!rs.isBeforeFirst()) return null;
            while(rs.next()) {
                Drzava d = nadjiDrzavu(rs.getString(2));
                if(d != null) rezultat.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezultat;
    }

    public void obrisiGrad(Grad grad) {
        try {
            System.out.println(grad.getNaziv());
            obrisiGradUpit.setString(1, grad.getNaziv());
            obrisiGradUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Grad nadjiGrad(String grad) {
        ArrayList<Grad> rezultat = gradovi();
        return gradovi().stream().filter(g -> g.getNaziv().equals(grad)).collect(Collectors.toList()).get(0);
    }
}
