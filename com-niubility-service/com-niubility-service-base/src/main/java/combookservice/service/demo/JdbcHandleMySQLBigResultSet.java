package combookservice.service.demo;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
由于生成报表逻辑要从数据库读取大量数据并在内存中加工处理后再生成大量的汇总数据然后写入到数据库。基本流程是 读取->处理->写入。
读取操作开始遇到的问题是当sql查询数据量比较大时候基本读不出来。开始以为是server端处理太慢。但是在控制台是可以立即返回数据的。于是在应用这边抓包，
 发现也是发送sql后立即有数据返回。但是执行ResultSet的next方法确实阻塞的。查文档翻代码原来mysql驱动默认的行为是需要把整个结果全部读取到内存中才开始允许应用读取结果
 显然与期望的行为不一致，期望的行为是流的方式读取，当结果从myql服务端返回后立即还是读取处理。这样应用就不需要大量内存来存储这个结果集。正确的流式读取方式代码示例：
*/
public class JdbcHandleMySQLBigResultSet {
    public static long importData(String sql) {
        String url = "jdbc:mysql://localhost:3306/test?user=root&password=111111&serverTimezone=UTC";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        long allStart = System.currentTimeMillis();
        long count = 0;

        Connection con = null;
        PreparedStatement ps = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection(url);
            //有三个条件forward-only，read-only，fatch size是Integer.MIN_VALUE
            ps = (PreparedStatement) con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);
            // ps = (PreparedStatement) con.prepareStatement(sql);
            ps.setFetchSize(Integer.MIN_VALUE);
            ps.setFetchDirection(ResultSet.FETCH_REVERSE);

            rs = ps.executeQuery();
            List list = new ArrayList();
            ResultSetMetaData md = rs.getMetaData();//获取键名
            int columnCount = md.getColumnCount();//获取行的数量
            while (rs.next()) {
                //此处处理业务逻辑
                count++;
                Map rowData = new HashMap();//声明Map
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), rs.getObject(i));//获取键名及值
                }
                list.add(rowData);
                System.out.println("list:" + list.toString());
            }
            System.out.println("取回数据量为  " + count + " 行！");
            /*list.forEach(a -> {
                System.out.println("list:" + a);
            });*/

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;

    }

    public static void main(String[] args) throws InterruptedException {
        String sql = "select * from test";
        importData(sql);

    }
}
