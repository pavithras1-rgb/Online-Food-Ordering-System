package com.quickbite;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class FoodOrderingApp extends JFrame {
    private static final String DB = "jdbc:sqlite:food_ordering.db";
    private final Map<String, Food> menu = new LinkedHashMap<>();
    private final Map<String, Integer> cart = new LinkedHashMap<>();
    private final DefaultListModel<String> cartModel = new DefaultListModel<>();
    private final JList<String> cartList = new JList<>(cartModel);
    private final JLabel totalLabel = new JLabel("Total: ₹0");
    private final JTextField name = new JTextField(), phone = new JTextField();
    private final JTextArea address = new JTextArea(3, 18);
    private record Food(String name, int price, String category) {}

    public FoodOrderingApp() {
        super("QuickBite - Food Ordering System");
        menu.put("Margherita Pizza", new Food("Margherita Pizza",249,"Pizza"));
        menu.put("Veg Burger", new Food("Veg Burger",129,"Burger"));
        menu.put("Masala Dosa", new Food("Masala Dosa",90,"South Indian"));
        menu.put("Paneer Wrap", new Food("Paneer Wrap",159,"Wraps"));
        menu.put("French Fries", new Food("French Fries",99,"Sides"));
        menu.put("Chocolate Brownie", new Food("Chocolate Brownie",110,"Dessert"));
        initDatabase();
        buildUI();
    }

    private void initDatabase() {
        try (Connection c = DriverManager.getConnection(DB); Statement s = c.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS orders (id INTEGER PRIMARY KEY AUTOINCREMENT, customer_name TEXT NOT NULL, phone TEXT NOT NULL, address TEXT NOT NULL, items TEXT NOT NULL, total INTEGER NOT NULL, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");
        } catch (SQLException e) { error("Database error: " + e.getMessage()); }
    }

    private void buildUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(900,620); setLocationRelativeTo(null);
        setLayout(new BorderLayout(12,12));
        JLabel header = new JLabel("  QuickBite | Online Food Ordering");
        header.setOpaque(true); header.setBackground(new Color(48,83,67)); header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif",Font.BOLD,23)); header.setBorder(new EmptyBorder(18,12,18,12));
        add(header,BorderLayout.NORTH);
        JPanel body = new JPanel(new GridLayout(1,2,14,14)); body.setBorder(new EmptyBorder(5,18,18,18));
        body.add(menuPanel()); body.add(cartPanel()); add(body,BorderLayout.CENTER);
        JLabel foot = new JLabel("Academic demo application - no real payment is collected",SwingConstants.CENTER);
        add(foot,BorderLayout.SOUTH);
    }

    private JPanel menuPanel() {
        JPanel p = panel(); p.setLayout(new BorderLayout(8,8));
        JLabel title = new JLabel("Food Menu"); title.setFont(new Font("SansSerif",Font.BOLD,20)); p.add(title,BorderLayout.NORTH);
        JPanel list = new JPanel(); list.setLayout(new BoxLayout(list,BoxLayout.Y_AXIS));
        for (Food f : menu.values()) {
            JPanel row = new JPanel(new BorderLayout(8,4)); row.setBorder(new EmptyBorder(10,10,10,10));
            row.setBackground(new Color(255,249,242));
            row.add(new JLabel("<html><b>"+f.name()+"</b><br>"+f.category()+" · ₹"+f.price()+"</html>"));
            JButton add = new JButton("Add +"); add.addActionListener(e -> { cart.merge(f.name(),1,(a,b)->Math.min(a+b,20)); refreshCart(); });
            row.add(add,BorderLayout.EAST); row.setMaximumSize(new Dimension(Integer.MAX_VALUE,62));
            list.add(row); list.add(Box.createVerticalStrut(8));
        }
        p.add(new JScrollPane(list)); return p;
    }

    private JPanel cartPanel() {
        JPanel p = panel(); p.setLayout(new BorderLayout(8,8));
        JLabel title = new JLabel("Your Cart"); title.setFont(new Font("SansSerif",Font.BOLD,20)); p.add(title,BorderLayout.NORTH);
        p.add(new JScrollPane(cartList),BorderLayout.CENTER);
        JPanel bottom = new JPanel(); bottom.setLayout(new BoxLayout(bottom,BoxLayout.Y_AXIS));
        totalLabel.setFont(new Font("SansSerif",Font.BOLD,18)); bottom.add(totalLabel);
        bottom.add(new JLabel("Customer name")); name.setMaximumSize(new Dimension(Integer.MAX_VALUE,30)); bottom.add(name);
        bottom.add(new JLabel("Phone (10-15 digits)")); phone.setMaximumSize(new Dimension(Integer.MAX_VALUE,30)); bottom.add(phone);
        bottom.add(new JLabel("Delivery address")); address.setLineWrap(true); address.setWrapStyleWord(true); bottom.add(new JScrollPane(address));
        JPanel buttons = new JPanel();
        JButton remove = new JButton("Remove selected"); remove.addActionListener(e -> removeSelected());
        JButton place = new JButton("Place order"); place.addActionListener(e -> placeOrder());
        buttons.add(remove); buttons.add(place); bottom.add(buttons); p.add(bottom,BorderLayout.SOUTH);
        return p;
    }

    private JPanel panel() {
        JPanel p = new JPanel(); p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230,222,212)),new EmptyBorder(14,14,14,14)));
        return p;
    }

    private void refreshCart() {
        cartModel.clear(); int total=0;
        for (var entry:cart.entrySet()) {
            Food f=menu.get(entry.getKey()); int line=f.price()*entry.getValue(); total+=line;
            cartModel.addElement(f.name()+" x "+entry.getValue()+" = ₹"+line);
        }
        totalLabel.setText("Total: ₹"+total);
    }

    private void removeSelected() {
        int i=cartList.getSelectedIndex();
        if(i<0){ error("Select an item to remove."); return; }
        cart.remove((String)cart.keySet().toArray()[i]); refreshCart();
    }

    private void placeOrder() {
        String customer=name.getText().trim(), mobile=phone.getText().trim(), delivery=address.getText().trim();
        if(cart.isEmpty()){error("Add at least one food item.");return;}
        if(customer.length()<2){error("Enter a valid name.");return;}
        if(!mobile.matches("\\d{10,15}")){error("Phone must contain 10 to 15 digits.");return;}
        if(delivery.length()<8){error("Enter a complete delivery address.");return;}
        StringBuilder items=new StringBuilder(); int total=0;
        for(var entry:cart.entrySet()){
            Food f=menu.get(entry.getKey()); int line=f.price()*entry.getValue(); total+=line;
            items.append(f.name()).append(" x ").append(entry.getValue()).append(" = ₹").append(line).append("\\n");
        }
        try(Connection c=DriverManager.getConnection(DB);
            PreparedStatement ps=c.prepareStatement("INSERT INTO orders(customer_name,phone,address,items,total) VALUES(?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1,customer);ps.setString(2,mobile);ps.setString(3,delivery);ps.setString(4,items.toString());ps.setInt(5,total);ps.executeUpdate();
            int id=-1;try(ResultSet keys=ps.getGeneratedKeys()){if(keys.next())id=keys.getInt(1);}
            JOptionPane.showMessageDialog(this,"Order placed!\\nOrder number: "+id+"\\nTotal: ₹"+total);
            cart.clear();refreshCart();name.setText("");phone.setText("");address.setText("");
        }catch(SQLException e){error("Could not save order: "+e.getMessage());}
    }
    private void error(String message){JOptionPane.showMessageDialog(this,message,"QuickBite",JOptionPane.ERROR_MESSAGE);}
    public static void main(String[] args){SwingUtilities.invokeLater(()->new FoodOrderingApp().setVisible(true));}
}
