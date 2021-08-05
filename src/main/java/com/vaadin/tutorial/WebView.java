package com.vaadin.tutorial;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.shared.Registration;
import com.vaadin.annotations.Theme;
import com.vaadin.resource.UpdateEvent;
import com.vaadin.resource.ConceptOrder;
import com.vaadin.resource.LayoutObject;
import com.vaadin.shared.ui.ContentMode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.tools.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Route("web")
@Push
@CssImport("./styles/main.css")
@CssImport(value = "./styles/menu-buttons.css", themeFor = "vaadin-button")
public class WebView extends VerticalLayout {
    public static List<ConceptOrder> apiOrders = new ArrayList<>();
    private static H1 myH1 = new H1("Enterprise Service Boutique WEB");
    private static H1 storeH1 = new H1("");
    private static VerticalLayout topLayout = new VerticalLayout();
    private static VerticalLayout storeVLayout = new VerticalLayout();
    private static HorizontalLayout panelLayout = new HorizontalLayout();
    private static HorizontalLayout storeHLayout = new HorizontalLayout();
    private static int globalCount = 0;
    private static int globalChangePoint = 2;
    private static int globalLine = 1;
    private static String globalTheme = "blueDom";
    private static String globalColor = "white";
    private static int localCount = 0;
    private static Map<Integer, String> colorDicto = new HashMap<>();
    private static Map<Integer, String> themeDicto = new HashMap<>();

    public WebView() throws IOException {
        globalCount = 0;
        String newColor = (String) UI.getCurrent().getSession().getAttribute("globalColor");
        if (newColor != null) {
            System.out.println("WebView newColor: " + newColor);
            globalColor = newColor;
        }
        String newTheme = (String) UI.getCurrent().getSession().getAttribute("globalTheme");
        if (newTheme != null) {
            System.out.println("WebView newTheme: " + newTheme);
            globalTheme = newTheme;
        }
        Integer newLine = (Integer) UI.getCurrent().getSession().getAttribute("globalLine");
        if (newLine != null) {
            System.out.println("WebView newLine: " + newLine);
            globalLine = newLine;
        }
        Integer newChangePoint = (Integer) UI.getCurrent().getSession().getAttribute("globalChangePoint");
        if (newChangePoint != null) {
            System.out.println("WebView newChangePoint: " + newChangePoint);
            globalChangePoint = newChangePoint;
        }
        List<ConceptOrder> sessionOrders = (List<ConceptOrder>) UI.getCurrent().getSession().getAttribute("conceptOrders");
        if (sessionOrders != null) {
            System.out.println("WebView sessionOrders: " + sessionOrders.size());
            apiOrders = sessionOrders;
        }
        topLayout = (VerticalLayout) buildForm();
        panelLayout = (HorizontalLayout) buildControlPane();
        if ((topLayout != null) && (panelLayout != null)) {

            myH1 = new H1("Enterprise Service Boutique WEB");
            System.out.println("WebView Constructor: " + this.localCount);
            if (this.localCount > 0) {
                System.out.println("WebView Replace: " + this.localCount + " |CmCount| " + this.getComponentCount());
                if (this.getComponentCount() > 0) {
                    this.remove(storeVLayout);
                    this.remove(storeHLayout);
                    this.remove(storeH1);
                }
                add(myH1, panelLayout, storeVLayout);
                replace(storeVLayout, topLayout);
                buildClassLayout(apiOrders);
                localCount += 1;
            }
        }
        if (this.localCount <= 0) {
            System.out.println("WebView-Normal UI create: " + apiOrders.size());
            topLayout = (VerticalLayout) buildForm();
            panelLayout = (HorizontalLayout) buildControlPane();
            myH1 = new H1("Enterprise Service Boutique WEB");
            this.getStyle().set("color", "red");
            this.setClassName(globalColor);
            for (int i = 1; i <= apiOrders.size(); i++) {
                themeDicto.put(i, globalTheme);
                colorDicto.put(i, globalColor);
            }
            add(myH1,
                    panelLayout,
                    topLayout);
            buildClassLayout(apiOrders);
            localCount += 1;
        }
        storeVLayout = topLayout;
        storeHLayout = panelLayout;
        storeH1 = myH1;
    }

    private Component buildControlPane() {
        setSizeFull();
        List<Integer> theGroups = new ArrayList<>();
        for (int i = 1; i <= apiOrders.size(); i++) {
            theGroups.add(i);
        }
        ComboBox<Integer> groupSelect = new ComboBox<>("Grouping", theGroups);
        List<Integer> theLine = new ArrayList<>();
        for (int i = 1; i < apiOrders.size(); i++) {
            theLine.add(i);
        }
        ComboBox<Integer> lineSelect = new ComboBox<>("Line", theLine);
        Button groupingButton = new Button("ReLoad");
        groupingButton.setThemeName("primary");
        List<String> themes = new ArrayList<>();
        themes.add("BlueTheme");
        themes.add("GreenTheme");
        themes.add("GrayTheme");
        themes.add("RedTheme");
        themes.add("BlackTheme");
        ComboBox<String> themeSelect = new ComboBox<>("Themes", themes);
        List<String> thecolor = new ArrayList<>();
        thecolor.add("BlackColor");
        thecolor.add("WhiteColor");
        thecolor.add("GrayColor");
        thecolor.add("RedColor");
        thecolor.add("YellowColor");
        ComboBox<String> colorSelect = new ComboBox<>("Color", thecolor);
        HorizontalLayout paneWindow = new HorizontalLayout(groupSelect, lineSelect, themeSelect, colorSelect, groupingButton);
        paneWindow.setClassName("menu-header");
        paneWindow.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        themeSelect.addValueChangeListener(event -> {
            System.out.println("WebView-addThemeChangeListener: " + event.getValue());
            this.globalTheme = event.getValue();
            if (this.globalTheme.equalsIgnoreCase("BlueTheme")) {
                this.globalTheme = "blueDom";
            }
            if (this.globalTheme.equalsIgnoreCase("GreenTheme")) {
                this.globalTheme = "greenDom";
            }
            if (this.globalTheme.equalsIgnoreCase("GrayTheme")) {
                this.globalTheme = "grayDom";
            }
            if (this.globalTheme.equalsIgnoreCase("RedTheme")) {
                this.globalTheme = "redDom";
            }
            if (this.globalTheme.equalsIgnoreCase("BlackTheme")) {
                this.globalTheme = "blackDom";
            }
        });
        colorSelect.addValueChangeListener(event -> {
            System.out.println("WebView-addColorChangeListener: " + event.getValue());
            this.globalColor = event.getValue();
            if (this.globalColor.equalsIgnoreCase("BlackColor")) {
                this.globalColor = "black";
            }
            if (this.globalColor.equalsIgnoreCase("WhiteColor")) {
                this.globalColor = "white";
            }
            if (this.globalColor.equalsIgnoreCase("GrayColor")) {
                this.globalColor = "gray";
            }
            if (this.globalColor.equalsIgnoreCase("RedColor")) {
                this.globalColor = "red";
            }
            if (this.globalColor.equalsIgnoreCase("YellowColor")) {
                this.globalColor = "yellow";
            }
        });
        groupSelect.addValueChangeListener(event -> {
            System.out.println("WebView-addValueChangeListener: " + event.getValue());
            this.globalChangePoint = event.getValue();
        });
        lineSelect.addValueChangeListener(event -> {
            System.out.println("WebView-addLineChangeListener: " + event.getValue());
            this.globalLine = event.getValue();
        });
        groupingButton.addClickListener(click -> {
            System.out.println("WebView-addClickListener: " + globalChangePoint);
            UI.getCurrent().getSession().setAttribute("globalChangePoint", globalChangePoint);
            UI.getCurrent().getSession().setAttribute("globalTheme", globalTheme);
            UI.getCurrent().getSession().setAttribute("globalColor", globalColor);
            UI.getCurrent().getSession().setAttribute("globalLine", globalLine);
            if (themeDicto.containsKey(globalLine))
                themeDicto.replace(globalLine, globalTheme);
            else
                themeDicto.put(globalLine, globalTheme);
            if (colorDicto.containsKey(globalLine))
                colorDicto.replace(globalLine, globalColor);
            else
                colorDicto.put(globalLine, globalColor);
            System.out.println("WebView-colorDicto: " + colorDicto.toString());
            System.out.println("WebView-themeDicto: " + themeDicto.toString());
            UI.getCurrent().getPage().reload();
        });
        if (paneWindow == null) {
            System.out.println("buildControlPane: NULL");
            paneWindow = new HorizontalLayout();
        }
        return paneWindow;
    }

    private static Component buildForm() {
        Map<Integer, Component> formDictionary = new HashMap<>();
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setClassName("menu-header");
        Integer layoutIndex = 0;
        LayoutObject layoutObject = new LayoutObject();
        com.vaadin.flow.component.combobox.ComboBox<String> snackTypeSelect = new com.vaadin.flow.component.combobox.ComboBox<>("Type", Collections.emptyList());
        com.vaadin.flow.component.combobox.ComboBox<String> snackSelect = new com.vaadin.flow.component.combobox.ComboBox<>("Concept", Collections.emptyList());
        com.vaadin.flow.component.textfield.TextField nameField = new TextField("Name");
        com.vaadin.flow.component.combobox.ComboBox<String> targetSelect = new ComboBox<>("Target", Collections.emptyList());
        com.vaadin.flow.component.button.Button orderButton = new Button("Save");
        Div errorsLayout = new Div();
        System.out.println("buildForm-apiOrders: " + apiOrders.size() + " |themeDicto| " + themeDicto.size());
        if (themeDicto.size() < apiOrders.size()) {
            for (int i = 1; i <= apiOrders.size(); i++) {
                themeDicto.put(i, globalTheme);
                colorDicto.put(i, globalColor);
            }
        }
        System.out.println("buildForm-colorDicto: " + colorDicto.toString() + " || " + globalColor);
        System.out.println("buildForm-themeDicto: " + themeDicto.toString() + " || " + globalTheme);
//        verticalLayout.addClickListener(e -> {
//            System.out.println("addClickListener");
//        });
        //Div wrapperLayout = new Div(formLayout, errorsLayout);
        //wrapperLayout.setWidth("100%");
        //wrapperLayout.addClickListener(e -> System.out.println("click"));
        formDictionary = (Map<Integer, Component>) buildFieldLayout(verticalLayout, apiOrders, 0);
        layoutObject = getLayoutObject(formDictionary);
        layoutIndex = layoutObject.getNumber();
        //verticalLayout = (VerticalLayout)layoutObject.getComponent();
        //formDictionary = (Map<Integer, Component>)buildSchemaLayout(verticalLayout, apiOrders, layoutIndex);
        //layoutObject = getLayoutObject(formDictionary);
        //layoutIndex = layoutObject.getNumber();
        verticalLayout = (VerticalLayout) layoutObject.getComponent();
        verticalLayout.setWidth("100%");
        if (verticalLayout == null) {
            System.out.println("buildForm: NULL");
            verticalLayout = new VerticalLayout();
        }
        return verticalLayout;
    }

    private static Map<Integer, Component> buildFieldLayout(VerticalLayout verticalLayout, List<ConceptOrder> sOrders, Integer layoutIndex) {
        Map<Integer, Component> formDictionary = new HashMap<>();
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        com.vaadin.flow.component.combobox.ComboBox<String> snackSelect = new com.vaadin.flow.component.combobox.ComboBox<>("Concept", Collections.emptyList());
        com.vaadin.flow.component.textfield.TextField nameField = new TextField("Name");
        String cssColor = globalColor;
        String cssTheme = globalTheme;
        System.out.println("buildFieldLayout default colors: " + cssColor + " theme: " + cssTheme);
        int counter = 1;
        for (ConceptOrder temp : sOrders) {
            String name = temp.getName();
            String concept = temp.getConcept();
            String theType = temp.getThetype();
            int colorIndex = layoutIndex + 1;
            if (colorDicto.size() > 0)
                cssColor = colorDicto.get(colorIndex);
            if (themeDicto.size() > 0)
                cssTheme = themeDicto.get(colorIndex);
            System.out.println("buildFieldLayout loaded colors: " + cssColor + " theme: " + cssTheme);
            System.out.println("_WebViewFieldLayout" + counter + " " + temp.getName() + " type: " + theType + " concept: " + concept);
            if ((theType.equalsIgnoreCase("property")) && (concept.equalsIgnoreCase("string-field"))) {
                nameField = new TextField(name);
                nameField.setClassName(cssTheme);
                nameField.getStyle().set("color", cssColor);
                formLayout.add(nameField);
                System.out.println("_WebViewFieldLayout_textfield: " + layoutIndex + " " + temp.getName());
                globalCount += 1;
                if (globalCount >= globalChangePoint) {
                    verticalLayout.addComponentAtIndex(layoutIndex, formLayout);
                    formLayout = new HorizontalLayout();
                    formLayout.removeClassName(cssColor);
                    formLayout.addClassName(cssColor);
                    layoutIndex += 1;
                    globalCount = 0;
                }
            }
            if ((theType.equalsIgnoreCase("property")) && (concept.equalsIgnoreCase("double-field"))) {
                nameField = new TextField(name);
                nameField.setClassName(cssTheme);
                nameField.getStyle().set("color", cssColor);
                formLayout.add(nameField);
                System.out.println("_WebViewFieldLayout_textfield: " + layoutIndex + " " + temp.getName());
                globalCount += 1;
                if (globalCount >= globalChangePoint) {
                    verticalLayout.addComponentAtIndex(layoutIndex, formLayout);
                    formLayout = new HorizontalLayout();
                    formLayout.removeClassName(cssColor);
                    formLayout.addClassName(cssColor);
                    layoutIndex += 1;
                    globalCount = 0;
                }
            }
            if ((theType.equalsIgnoreCase("property")) && (concept.equalsIgnoreCase("choice-field"))) {
                snackSelect = new com.vaadin.flow.component.combobox.ComboBox<>(name, Collections.emptyList());
                //nameField.setValue(temp.getName());
                formLayout.add(snackSelect);
                System.out.println("_WebViewFieldLayout_choice: " + layoutIndex + " " + temp.getName());
                globalCount += 1;
                if (globalCount >= globalChangePoint) {
                    verticalLayout.addComponentAtIndex(layoutIndex, formLayout);
                    formLayout = new HorizontalLayout();
                    formLayout.addClassName(globalColor);
                    layoutIndex += 1;
                    globalCount = 0;
                }
            }
            if ((theType.equalsIgnoreCase("property")) && (concept.equalsIgnoreCase("name-title"))) {
                nameField = new TextField(name);
                nameField.setClassName(cssTheme);
                nameField.getStyle().set("color", cssColor);
                //nameField.setValue(temp.getName());
                formLayout.add(nameField);
                System.out.println("_WebViewFieldLayoutNTitle_textfield: " + layoutIndex + " " + temp.getName());
                globalCount += 1;
                if (globalCount >= globalChangePoint) {
                    verticalLayout.addComponentAtIndex(layoutIndex, formLayout);
                    formLayout = new HorizontalLayout();
                    formLayout.removeClassName(cssColor);
                    formLayout.addClassName(cssColor);
                    layoutIndex += 1;
                    globalCount = 0;
                }
            }
            if ((theType.equalsIgnoreCase("property")) && (concept.equalsIgnoreCase("description"))) {
                nameField = new TextField(name);
                nameField.setClassName(cssTheme);
                nameField.getStyle().set("color", cssColor);
                //nameField.setValue(temp.getName());
                formLayout.add(nameField);
                System.out.println("_WebViewFieldLayout_textfield: " + layoutIndex + " " + temp.getName());
                globalCount += 1;
                if (globalCount >= globalChangePoint) {
                    verticalLayout.addComponentAtIndex(layoutIndex, formLayout);
                    formLayout = new HorizontalLayout();
                    formLayout.removeClassName(cssColor);
                    formLayout.addClassName(cssColor);
                    layoutIndex += 1;
                    globalCount = 0;
                }
            }
            counter += 1;
        }
        if (layoutIndex > 0) {
            formLayout = new HorizontalLayout();
            Span span = new Span();
            span.add(new Label(""));
            formLayout.add(span);
            span.add(new Label(""));
            formLayout.add(span);
            span.add(new Label(""));
            formLayout.add(span);
            Button crudButton = new Button("SAVE");
            crudButton.setThemeName("primary");
            span.add(crudButton);
            formLayout.add(span);
        }
        verticalLayout.addComponentAtIndex(layoutIndex, formLayout);
        if (verticalLayout == null) {
            System.out.println("buildFieldLayout: NULL");
            verticalLayout = new VerticalLayout();
        }
        layoutIndex += 1;
        formDictionary.put(layoutIndex, verticalLayout);
        //verticalLayout.removeClassName(cssTheme);
        verticalLayout.addClassName("red");
        return formDictionary;
    }

    private void buildClassLayout(List<ConceptOrder> sOrders) throws IOException {
        String todaySource = ".java";
        String classField = "";
        String argumentField = "";
        Date today = new Date();
        String todayMillis = Long.toString(today.getTime());
        String entityClass = "z_" + todayMillis;
        String entitySource = "z_" + todayMillis;
        String entityOutput = "";
        String entityMethod = "";
        String entityArgs = "";
        String entityInterface = "";
        String parentInterface = "";
        List<ConceptOrder> schemaItem = sOrders.stream().filter(p -> p.getThetype().equalsIgnoreCase("schema")).collect(Collectors.toList());;
        int counter = 100;
        for (ConceptOrder temp : sOrders) {
            String name = temp.getName();
            String concept = temp.getConcept();
            String theType = temp.getThetype();
            String theInterface = temp.getIinterface();
            System.out.println("buildClassLayout" + counter + " " + temp.getName() + " type: " + theType + " concept: " + concept);
            if ((theType.equalsIgnoreCase("entity")) && (concept.equalsIgnoreCase("product"))) {
                counter = 100;
                theInterface = StringUtils.capitalize(theInterface);
                if ((theInterface.length() > 0) && (!theInterface.equalsIgnoreCase("None"))) {
                    parentInterface = getContentFromRepo(theInterface);
                    entityOutput += parentInterface + "\n\n";
                    entityInterface = " implements " + theInterface + " ";
                } else {
                    entityInterface = "";
                }
                if((schemaItem!=null)&&(schemaItem.size()>0)){
                    entityClass =schemaItem.get(0).getName() + "_" + todayMillis;
                }else {
                    entityClass = name + "_" + todayMillis;
                }
                //entityClass = name;
                String camelCase = StringUtils.capitalize(entityClass);
                entitySource = camelCase + ".java";
                entityOutput += "public class " + camelCase + entityInterface + "{\n";
                entityOutput += " public void test() {System.out.println(\"" + todayMillis + "\");}\n";
                todaySource = "d:/" + entitySource;
            }
            if ((theType.equalsIgnoreCase("property")) && (concept.equalsIgnoreCase("string-field"))) {
                counter = 200;
                String camelCase = StringUtils.capitalize(name);
                classField = "private String " + camelCase + ";\n";
                entityOutput += classField + "\n";
                entityOutput += "public String get" + camelCase + "() {return " + camelCase + ";}\n";
                entityOutput += "public void set" + camelCase + "(String " +camelCase + ") {this." + camelCase + " = " + camelCase + ";}\n";
                argumentField = "String " + camelCase + "||";
                entityArgs = entityArgs.replace("||", ",");
                entityArgs += argumentField;
            }
            if ((theType.equalsIgnoreCase("property")) && (concept.equalsIgnoreCase("double-field"))) {
                counter = 200;
                String camelCase = StringUtils.capitalize(name);
                classField = "private Double " + camelCase+ ";\n";
                entityOutput += classField + "\n";
                entityOutput += "public Double get" + camelCase + "() {return " + camelCase + ";}\n";
                entityOutput += "public void set" + camelCase + "(Double " + camelCase + ") {this." + camelCase + " = " + camelCase + ";}\n";
                argumentField = "Double " + camelCase + "||";
                entityArgs = entityArgs.replace("||", ",");
                entityArgs += argumentField;
            }
            if ((theType.equalsIgnoreCase("function")) && (concept.equalsIgnoreCase("interface"))) {
                counter = 200;
                System.out.println("buildClassLayout1-args" + entityArgs);
                String args = entityArgs.replace("||", "");
                System.out.println("buildClassLayout2-args" + args);
                entityMethod = StringUtils.capitalize(name);
                String returnType = temp.getIinterface();
                classField = "public " + returnType + " " + entityMethod + "(" + args + "){\n";
                if (returnType.equalsIgnoreCase("boolean")) {
                    classField += " System.out.println(\"" + entityMethod + "-" + todayMillis + "\");\n";
                    classField += " return false;}\n";
                } else if (returnType.equalsIgnoreCase("string")) {
                    classField += " System.out.println(\"" + entityMethod + "-" + todayMillis + "\");\n";
                    classField += " return \"\";}\n";
                } else if (returnType.equalsIgnoreCase("double")) {
                    classField += " System.out.println(\"" + entityMethod + "-" + todayMillis + "\");\n";
                    classField += " return 0.0'}\n";
                }
                entityOutput += "@Override \n";
                entityOutput += classField;
            }
        }
        entityOutput += " }\n";
        if (entityOutput.length() > 16) {
            try {
                FileWriter aWriter = new FileWriter(todaySource, true);
                System.out.println("buildClassLayout-Exit " + entityOutput);
                aWriter.write(entityOutput);
                aWriter.flush();
                aWriter.close();
                String[] source = {new String(todaySource)};
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                System.out.println("buildClassLayout-CAX " + source[0]);
                if (compileIt(todaySource, entityClass, entityMethod) == 0) {
                    System.out.println("Running " + todaySource + ":\n\n");
                } else {
                    System.out.println(todaySource + " is bad.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static LayoutObject getLayoutObject(Map<Integer, Component> formDictionary) {
        LayoutObject layoutObject = new LayoutObject();
        for (Map.Entry<Integer, Component> entry : formDictionary.entrySet()) {
            layoutObject.setNumber(entry.getKey());
            layoutObject.setComponent(entry.getValue());
        }
        return layoutObject;
    }

    public static void loadData(List<ConceptOrder> newOrders) {
/*        System.out.println("Loaded: " + localCount + " x| " + newOrders.size() + " records");
        apiOrders = newOrders;
        if (localCount > 0) {
            System.out.println("fireEvent: " + localCount + " x| " + newOrders.size() + " records");
            ComponentUtil.fireEvent(new WebView(), new UpdateEvent(new WebView(), false, newOrders.size()));
            localCount += 1;
        }*/
    }

    public Registration addUpdateListener(ComponentEventListener<UpdateEvent> listener) {
        return addListener(UpdateEvent.class, listener);
    }

    private ComponentEventListener<UpdateEvent> onUpdate(UpdateEvent event) {
//        System.out.println("WebView onUpdate [" + event.getMessage() + "]");
//        replace(storeLayout, topLayout);
        //UI.getCurrent().getPage().reload();
        return null;
    }

    public int compileIt(String thisSource, String className, String methodName) {
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        String javacOpts[] = {thisSource};
        if (javac.run(null, null, null, javacOpts) != 0) {
            throw new RuntimeException("compilation of " + thisSource + ".java Failed");
        }
        runClassMethod(thisSource, className, methodName);
        return 0;
    }

    public void runClassMethod(String thisSource, String className, String methodName) {
        try {
            URL[] url = new URL[]{new File(thisSource).getParentFile().toURI().toURL()};
            Class params[] = {};
            Object paramsObj[] = {};
            ClassLoader cl = new URLClassLoader(url);
            Class thisClass = cl.loadClass(className);
            //Class thisClass = Class.forName(className);
            Object iClass = thisClass.newInstance();
            Method thisMethod = thisClass.getDeclaredMethod(methodName, params);
            thisMethod.invoke(iClass, paramsObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getContentFromRepo(String fileName) throws IOException {
        fileName = "d:/za.mofulet/_sb_dynaminrepo/" + fileName + ".txt";
        return FileUtils.readFileToString(new File(fileName), StandardCharsets.UTF_8);
    }
}

