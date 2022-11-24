import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
    Connection conn;
    Statement stmt;
    ResultSet result;
    PreparedStatement pstmt;
    private Integer id;

    public DB() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            try {
                conn = DriverManager.getConnection("jdbc:mariadb://localhost/apotek", "root", "root");
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ResultSet tampilObat() {
        /*
         * Menampilkan seluruh data obat pada database.
         */
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String sql = "SELECT * FROM obat";
        try {
            result = stmt.executeQuery(sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public ResultSet mendapatkanPenggunaDariUsername(String username) {
        try {
            pstmt = conn.prepareStatement("SELECT * FROM pelanggan WHERE username=?");
            pstmt.setString(1, username);
            result = pstmt.executeQuery();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public void tambahPelanggan(String nama, String username, Integer umur, String jenisKelamin, String alamat) {
        String sql = String.format(
                "INSERT INTO pelanggan (nama, username, umur, jenis_kelamin, alamat) VALUES ('%s', '%s', '%d', '%s', '%s')",
                nama, username, umur, jenisKelamin, alamat);
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public PreparedStatement mendapatkanObatDariID(Integer id) {
        try {
            pstmt = conn.prepareStatement("SELECT * FROM obat WHERE id=?");
            pstmt.setInt(1, id);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pstmt;
    }

    public Integer simpanTransaksi(Integer idObat, Integer totalBayar, Integer totalBarang, Integer totalKembalian) {
        String columnNames[] = new String[] { "id" };
        try {
            pstmt = conn.prepareStatement(
                    "INSERT INTO transaksi (id_obat, total_bayar, total_barang, total_kembalian) VALUES (?, ?, ?, ?)",
                    columnNames);
            pstmt.setInt(1, idObat);
            pstmt.setInt(2, totalBayar);
            pstmt.setInt(3, totalBarang);
            pstmt.setInt(4, totalKembalian);

            if (pstmt.executeUpdate() > 0) {
                pstmt.setInt(4, totalKembalian);
                result = pstmt.getGeneratedKeys();
                if (result.next()) {
                    id = result.getInt(1);
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return id;
    }

    public ResultSet mendapatkanTransaksiDariID(Integer id) {
        /*
         * Mendapatkan transaksi menurut id
         */
        try {
            pstmt = conn.prepareStatement("SELECT * FROM transaksi WHERE id=?");
            pstmt.setInt(1, id);
            result = pstmt.executeQuery();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public void simpanTransaksiDetail(Integer idTransaksi, Integer idPelanggan) {
        try {
            pstmt = conn.prepareStatement(
                    "INSERT INTO `transaksi_detail`(`id_transaksi`, `id_pelanggan`) VALUES (?, ?)");
            pstmt.setInt(1, idTransaksi);
            pstmt.setInt(2, idPelanggan);
            pstmt.executeQuery();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void tampilTransaksiDetail(Integer idPengguna) {
        try {
            pstmt = conn.prepareStatement(
                    "SELECT * FROM transaksi INNER JOIN transaksi_detail ON transaksi.id=transaksi_detail.id_transaksi WHERE transaksi_detail.id_pelanggan=?;");
            pstmt.setInt(1, idPengguna);
            result = pstmt.executeQuery();

            if (result.next()) {
                System.out.println(result.getString("total_kembalian"));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
