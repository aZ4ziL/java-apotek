import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Formatter;

public class Menu {
    ResultSet result;

    Boolean isLogined;
    String username;
    String namaLengkap;
    Integer idPengguna;
    Integer umurPengguna;
    String jenisKelaminPengguna;
    String alamatPengguna;
    // Input user
    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    public void login() throws IOException, SQLException {
        /*
         * Fungsi ini akan menanyakan kepada user, apakah si user sudah mempunyai akun.
         * Jika belum maka user akan di arahkan kepada pendaftaran pengguna.
         * Jika sudah maka user akan di arahkan kepada login.
         */

        DB db = new DB();

        System.out.print("Selamt Datang Di Apotek");
        System.out.println();
        System.out.println("Apakah anda sudah mempunyai akun?");
        System.out.print("[Y/N] => ");
        String pilihan = input.readLine();

        switch (pilihan) {
            case "Y":
                // Clear teminal
                System.out.print("\033[H\033[2J");
                System.out.flush();

                System.out.print("Username => ");
                username = input.readLine();
                System.out.print("Password => ");
                String password = input.readLine();

                ResultSet userObject = db.mendapatkanPenggunaDariUsername(username);
                if (userObject.next()) {
                    // System.out.println(userObject.getString("password"));
                    if (password.contains(userObject.getString("password"))) {
                        namaLengkap = userObject.getString("nama");
                        umurPengguna = userObject.getInt("umur");
                        jenisKelaminPengguna = userObject.getString("jenis_kelamin");
                        alamatPengguna = userObject.getString("alamat");
                        idPengguna = userObject.getInt("id");
                        isLogined = true;
                        while (isLogined) {
                            menuUtama();
                        }
                    } else {
                        System.out.println("Username atau katasandi yang anda masukkan salah.");
                        System.exit(0);
                    }
                }
                break;
            case "N":
                System.out.println("Selamat tinggal.");
                System.exit(0);
                break;
        }
    }

    public void menuUtama() throws NumberFormatException, IOException, SQLException {
        System.out.println("Login Sebagai\t:\t" + username + "\t\t\tNama Lengkap\t:\t" + namaLengkap + "\n");
        System.out.println("1. Tampil Obat");
        System.out.println("2. Beli Obat");
        System.out.println("3. Lihat transaksi");
        System.out.println("4. Keluar");
        System.out.print("=> ");
        Integer pilihan = Integer.parseInt(input.readLine());

        switch (pilihan) {
            case 1:
                menuTampilObat();
                break;
            case 2:
                menuBeliObat();
                break;
            case 3:
                DB db = new DB();
                db.tampilTransaksiDetail(idPengguna);
                break;
            default:
                System.exit(0);
                break;
        }
    }

    public void menuTampilObat() {
        DB db = new DB();

        // Clear terminal
        System.out.print("\033[H\033[2J");
        System.out.flush();

        result = db.tampilObat();

        Formatter f = new Formatter();
        System.out.println("----------------------------------------------------");
        System.out.println(f.format("%s %10s %10s %10s", "ID", "Nama", "Harga", "Stok"));
        System.out.println("----------------------------------------------------");

        try {
            while (result.next()) {
                Formatter formatter = new Formatter();
                Integer id, harga, stok;
                String nama;
                id = result.getInt("id");
                harga = result.getInt("harga");
                stok = result.getInt("stok");
                nama = result.getString("nama");
                System.out.println(formatter.format("%s %10s %10s %10s", id, nama, harga, stok));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println();
        System.out.println();
    }

    public void menuBeliObat() throws IOException, SQLException {
        // String nama, jenisKelamin, alamat;
        // Integer umur;

        // System.out.println("Mohon isi data sebelum membeli!");
        // System.out.println();
        // // Input nama lengkap
        // System.out.print("Nama lengkap => ");
        // nama = input.readLine();
        // // input umur
        // System.out.print("Umur => ");
        // umur = Integer.parseInt(input.readLine());
        // // input jenis kelamin
        // System.out.print("Jenis kelamin [L/P] => ");
        // jenisKelamin = input.readLine();
        // // input alamat
        // System.out.print("Alamat => ");
        // alamat = input.readLine();
        // DB db = new DB();
        // db.tambahPelanggan(namaLengkap, username, umurPengguna, jenisKelaminPengguna,
        // alamatPengguna);

        DB db = new DB();
        // // Clear teminal
        // System.out.print("\033[H\033[2J");
        // System.out.flush();

        menuTampilObat();

        System.out.println("Silahkan pilih obat dengan memasukkan `ID` obat.");

        // Deklarasi variabel transaksi
        Integer idObat, totalBayar, totalBarang, totalKembalian;

        // Input IDobat
        System.out.print("ID Obat => ");
        idObat = Integer.parseInt(input.readLine());
        // Input totalBayar
        System.out.print("Total bayar Rp. => ");
        totalBayar = Integer.parseInt(input.readLine());
        // Input totalBarang
        System.out.print("Total barang => ");
        totalBarang = Integer.parseInt(input.readLine());

        PreparedStatement obatObject = db.mendapatkanObatDariID(idObat);
        result = obatObject.executeQuery();
        if (result.next()) {
            Integer harga = result.getInt("harga");
            totalKembalian = totalBayar - (harga * totalBarang);

            Integer idTransaksi = db.simpanTransaksi(idObat, totalBayar, totalBarang, totalKembalian);
            db.simpanTransaksiDetail(idTransaksi, idPengguna);

            System.out.println("Berhasil membeli barang.");
            input.readLine();
        }
    }
}
