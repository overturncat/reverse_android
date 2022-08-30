import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.List;

public class XMLUtils {
    /*
    * 输入： xml文件路径
    * 输出： Document 文件对象
    * */
    public static Document getDocument(String path){
        try {
            // 获取Document对象
            SAXReader reader = new SAXReader();
            FileInputStream in = new FileInputStream(path);
            Document doc = reader.read(in);
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /*输入： Document文件对象， 节点名
    * 输出： Element 节点对象
    * */
    public static Element getElement(Document doc, String name){
        Element root = doc.getRootElement();
        if(root.getName().equals(name))
            return root;
        return getChildElement(root,name);
    }
    /*输入： Element父节点， 子节点名
    * 输出： Element 节点对象
    * */
    public static Element getChildElement(Element node, String name){
        List<Element> childNodes = node.elements();
        for (Element e: childNodes){
            if(e.getName().equals(name))
                return e;
        }
        return null;
    }
    /*输入： Element 节点对象， 属性名
    * 输出： Attribute 属性对象
    * */
    public static Attribute getAttribute(Element node, String name){
        List<Attribute> attrs = node.attributes();
        for(Attribute attr: attrs){
            if(attr.getName().equals(name))
                return attr;
        }
        return null;
    }
    /*输入: AndroidManifest.xml文件路径
    * 输出：修改AndroidManifest.xml中的application节点的android:name, 指向傀儡dex*/
    public static void modifyApplication(String xmlPath, String dexPath){
        Document doc = XMLUtils.getDocument(xmlPath);
        Element eleApplication = XMLUtils.getElement(doc, "application");
        Attribute application_name = XMLUtils.getAttribute(eleApplication, "name");
        // 判断是否存在application:name , 存在则修改，不存在则创建
        if(application_name == null){
            // 不存在， 添加
            eleApplication.addAttribute("android:name", dexPath);
        } else{
            // 存在， 修改，保存原有的application:name
            String old_name = application_name.getValue();
            Element eleMata = eleApplication.addElement("meta-data");
            eleMata.addAttribute("android:name", "APPLICATION_CLASS_NAME");
            eleMata.addAttribute("android:value", old_name);
            // 设置新值
            application_name.setValue(dexPath);
        }
        try {
            // 保存修改的document到AndroidManifest.xml,
            File file = new File(xmlPath);
            // 删除重写
            if(file.exists())
                file.delete();
            file.createNewFile();
            XMLWriter out = new XMLWriter(new FileWriter(file));
            out.write(doc);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
