package com.vaadin.tutorial;

import com.vaadin.annotations.Theme;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dnd.GridDropEvent;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropEvent;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.*;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.shared.Registration;
import com.vaadin.tutorial.WebView;

import java.awt.event.ComponentEvent;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.component.html.Label;
import com.vaadin.resource.UpdateEvent;
import com.vaadin.resource.ConceptOrder;

@Route("")
@PWA(name = "My Application", shortName = "My Application")
@Push
@CssImport("./styles/main.css")
public class MainView extends VerticalLayout {

    public List<ConceptOrder> conceptOrders = new LinkedList<>();
    private Grid<ConceptOrder> conceptOrderGrid = new Grid<>(ConceptOrder.class);
    private List<String> emptylst = new ArrayList<>(Collections.nCopies(1, "Self"));
    private String pageTitle = "";
    private Button button;
    private VerticalLayout columnA;
    private VerticalLayout columnB;
    private VerticalLayout columnAWrapper;
    private VerticalLayout columnBWrapper;
    private Label labelA;
    private Label labelB;
    private boolean goToWebView = false;
    private BeforeEnterEvent thisBeforeEnterEvent;

    public MainView() {
        List<ConceptOrder> sessionOrders = (List<ConceptOrder>)UI.getCurrent().getSession().getAttribute("conceptOrders");
        if(sessionOrders!=null){
            System.out.println("sessionOrders: " + sessionOrders.size());
            conceptOrders = sessionOrders;
            conceptOrderGrid.setItems(conceptOrders);
            conceptOrderGrid.getDataProvider().refreshAll();
        }
        H1 myH1 = new H1("Enterprise Service Boutique");
        myH1.setHeight("25px");
        add(myH1, buildForm(), conceptOrderGrid);
        this.setSpacing(false);
    }

    private Component buildForm() {
        // The concepts we can choose from. In real life, these would
        // probably come from a service of some sort.
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        conceptOrderGrid.setRowsDraggable(true);
        conceptOrderGrid.setDropMode(GridDropMode.BETWEEN);
        conceptOrderGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        VerticalLayout topHWindow = new VerticalLayout();
        topHWindow.setSpacing(false);
        columnAWrapper = new VerticalLayout();
        columnAWrapper.addClassName("wrapper");
        columnA = new VerticalLayout();
        columnA.addClassNames("column", "column-a");
        labelA = new Label("Context");
        columnAWrapper.add(labelA, columnA);
        columnAWrapper.setWidth("50%");

        columnBWrapper = new VerticalLayout();
        columnBWrapper.addClassName("wrapper");
        columnB = new VerticalLayout();
        columnB.addClassNames("column", "column-b");
        labelB = new Label("Field");
        columnBWrapper.add(labelB, columnB);
        columnBWrapper.setWidth("50%");
        DropTarget.create(columnA).addDropListener(this::onADrop);
        DropTarget.create(columnB).addDropListener(this::onBDrop);

        conceptOrderGrid.addDragStartListener(e -> {
            System.out.println("start dragging: " + e);
        });
        conceptOrderGrid.addDragEndListener(e -> {
            System.out.println("end dragging: " + e);
        });
        conceptOrderGrid.addDropListener(e -> {
            System.out.println("drop: " + e);
            Set<ConceptOrder> salesOrgs = getConceptOrders(e);
        });
        HorizontalLayout dragHWindow = new HorizontalLayout(columnAWrapper, columnBWrapper);
        dragHWindow.setWidth("100%");

        Map<String, List<String>> concepts = new HashMap<>();
        concepts.put("Entity", Arrays.asList("Product", "Asset", "Idea", "Concept", "Person", "Entity", "Role"));
        concepts.put("Property", Arrays.asList("Name-Title", "Description", "Note", "Comment", "Example", "Annotation", "Metadata", "String-Field", "Date-Field", "Double-Field", "Choice-Field", "Number-Field"));
        concepts.put("Dimension", Arrays.asList("Price", "Height", "Weight", "Length", "Width", "Distance", "Price"));
        concepts.put("Schema", Arrays.asList("WebItem_T4", "Person_LN2", "Identity_J4W", "Address_9ZA", "Merchant_8YA", "Payment_VRD", "Finance_0R2", "Loan_PL8", "Deposit_NB4", "Customer_MF4", "WithDrawal_LK6", "Transfer_65B"));
        concepts.put("Interface", Arrays.asList( "Function", "Custom", "IAddress","IPerson", "Identity", "IMasterCard", "IVisa", "IAMExpress", "IDiscovery", "INedbank", "IFNB", "IABSA"));

        // Create UI components
        ComboBox<String> snackTypeSelect = new ComboBox<>("Type", concepts.keySet());
        ComboBox<String> snackSelect = new ComboBox<>("Concept", Collections.emptyList());
        TextField nameField = new TextField("Name");
        ComboBox<String> targetSelect = new ComboBox<>("Target", emptylst);
        Button orderButton = new Button("Save");
        Button closeButton = new Button("Clear");
        Div errorsLayout = new Div();

        // Configure UI components
        //quantityField.setPreventInvalidInput(true);
        orderButton.setEnabled(false);
        orderButton.setThemeName("primary");
        closeButton.setEnabled(true);
        closeButton.setThemeName("primary");

        // Only enable concept selection after a type has been selected.
        // Populate the concept alternatives based on the type.
        targetSelect.setEnabled(true);
        conceptOrderGrid.addItemClickListener(e -> {
            ConceptOrder ConceptOrder = e.getItem();
            emptylst = new ArrayList<>(Collections.nCopies(1, "Self"));
            targetSelect.setEnabled(ConceptOrder != null);
            if (ConceptOrder != null) {
                System.out.println("COLUMN: " + e.getColumn().getKey());
                Label labelB2 = labelB;
                labelB = new Label(e.getColumn().getKey());
                columnBWrapper.replace(labelB2, labelB);
                targetSelect.setValue("");
                emptylst.add(ConceptOrder.getName());
                String[] stritems = {"Self", ConceptOrder.getName()};
                emptylst = Arrays.asList(stritems);
                System.out.println("ROW: " + ConceptOrder.getName() + " |concept| " + ConceptOrder.getConcept());
                targetSelect.setItems(emptylst);
                Label labelA2 = labelA;
                labelA = new Label(ConceptOrder.getName());
                columnAWrapper.replace(labelA2, labelA);
            }
        });

        snackSelect.setEnabled(false);
        snackTypeSelect.addValueChangeListener(e -> {
            String type = e.getValue();
            snackSelect.setEnabled(type != null && !type.isEmpty());
            if (type != null && !type.isEmpty()) {
                pageTitle = type;
                snackSelect.setValue("");
                snackSelect.setItems(concepts.get(type));
            }
        });

        // Create bindings between UI fields and the ConceptOrder data model
        Binder<ConceptOrder> binder = new Binder<>(ConceptOrder.class);
        binder.forField(nameField)
                .asRequired("Name is required")
                .bind("name");
/*    binder.forField(quantityField)
        .asRequired()
        .withConverter(new StringToIntegerConverter("Quantity must be a number"))
        .withValidator(new IntegerRangeValidator("Quantity must be at least 1", 1, Integer.MAX_VALUE))
        .bind("quantity");*/
        binder.forField(snackSelect)
                .asRequired("Please choose a topic")
                .bind("concept");
        binder.forField(targetSelect)
                .asRequired("Please choose a target")
                .bind("target");
        // Only enable submit button when the form is valid.
        binder.addStatusChangeListener(status -> {
                    // Workaround for https://github.com/vaadin/flow/issues/4988
                    boolean emptyFields = Stream.of("name", "concept", "target")
                            .flatMap(prop -> binder.getBinding(prop).stream())
                            .anyMatch(binding -> binding.getField().isEmpty());
                    orderButton.setEnabled(!status.hasValidationErrors() && !emptyFields);
                }
        );


        // Process order
        orderButton.addClickListener(click -> {
            try {
                goToWebView = true;
                errorsLayout.setText("");
                ConceptOrder savedOrder = new ConceptOrder();
                binder.writeBean(savedOrder);
                addOrder(savedOrder);
                binder.readBean(new ConceptOrder());
                snackTypeSelect.setValue("");
            } catch (ValidationException e) {
                errorsLayout.add(new Html(e.getValidationErrors().stream()
                        .map(res -> "<p>" + res.getErrorMessage() + "</p>")
                        .collect(Collectors.joining("\n"))));
            }
        });

        closeButton.addClickListener(click -> {
            conceptOrders.clear();
            conceptOrderGrid.setItems(conceptOrders);
            conceptOrderGrid.getDataProvider().refreshAll();
        });

        // Wrap components in layouts
        HorizontalLayout formLayout = new HorizontalLayout(snackTypeSelect, snackSelect, nameField, targetSelect, orderButton, closeButton);
        Div wrapperLayout = new Div(formLayout, errorsLayout);
        formLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        wrapperLayout.setWidth("100%");
        topHWindow.add(dragHWindow, wrapperLayout);

        return topHWindow;
    }

    private void addOrder(ConceptOrder order) {
        System.out.println("MainView addOrder..." + pageTitle);
        order.setThetype("None");
        if (pageTitle.length() >= 0) {
            order.setThetype(pageTitle);
        }
        conceptOrders.add(order);
        ApiView.apiOrders = conceptOrders;
        conceptOrderGrid.setItems(conceptOrders);
        addSupportingOrders(order);
        conceptOrderGrid.getDataProvider().refreshAll();
        goToWebView = false;
        UI.getCurrent().getSession().setAttribute("conceptOrders", conceptOrders);
        fireEvent(new UpdateEvent(new VerticalLayout(), false, conceptOrders.size()));
        //WebView.loadData(conceptOrders);
    }

    private void onADrop(DropEvent<VerticalLayout> event) {
        if (event.getDragSourceComponent().isPresent()) {
            List<ConceptOrder> theConceptOrders = (List<ConceptOrder>) event.getDragData().orElse("");
            for (ConceptOrder thisConceptOrder : theConceptOrders) {
                String contextName = labelA.getText();
                System.out.println("onADrop: " + thisConceptOrder.getName() + " " + thisConceptOrder.getThetype());
                Label labelA2 = labelA;
                labelA = new Label("Add: " + thisConceptOrder.getName() + " To Context: " + labelA2.getText());
                columnAWrapper.replace(labelA2, labelA);
                List<ConceptOrder> result = conceptOrders
                        .stream()
                        .filter(p -> p.getName().equalsIgnoreCase(thisConceptOrder.getName()))
                        .collect(Collectors.toList());
                for (ConceptOrder co : conceptOrders) {
/*                    if (co.getName().equalsIgnoreCase(contextName)) {
                        co.setTarget("Self");
                    }*/
                    if (co.getName().equalsIgnoreCase(thisConceptOrder.getName())) {
                        co.setTarget(contextName);
                    }
                }
            }
/*      if ("From A".equals(dragData) && event.getComponent() == columnB) {
        columnA.remove(button);
        columnB.add(button);
        button.setText("Good! I'd like to go back to column A");
      } else if ("From B".equals(dragData) && event.getComponent() == columnA) {
        columnB.remove(button);
        columnA.add(button);
        button.setText("Nice! I'd like to go back to column B");
      }*/
        }
        conceptOrderGrid.getDataProvider().refreshAll();
    }

    private void onBDrop(DropEvent<VerticalLayout> event) {
        if (event.getDragSourceComponent().isPresent()) {
            List<ConceptOrder> conceptOrders = (List<ConceptOrder>) event.getDragData().orElse("");
            for (ConceptOrder conceptOrder : conceptOrders) {
                System.out.println("onBDrop: " + conceptOrder.getName() + " " + conceptOrder.getThetype());
                Label labelB2 = labelB;
                labelB = new Label("Add: " + conceptOrder.getName() + " To Field: " + labelB2.getText());
                columnBWrapper.replace(labelB2, labelB);
            }
/*      if ("From A".equals(dragData) && event.getComponent() == columnB) {
        columnA.remove(button);
        columnB.add(button);
        button.setText("Good! I'd like to go back to column A");
      } else if ("From B".equals(dragData) && event.getComponent() == columnA) {
        columnB.remove(button);
        columnA.add(button);
        button.setText("Nice! I'd like to go back to column B");
      }*/
        }
        conceptOrderGrid.getDataProvider().refreshAll();
    }

    private Set<ConceptOrder> getConceptOrders(GridDropEvent<ConceptOrder> e) {
        System.out.println("getConceptOrders: " + e.getDataTransferData().toString());
        Set<ConceptOrder> conceptOrders = new HashSet<>();
        Optional<String> data = e.getDataTransferData("text/plain");
        if (data.isPresent()) {
            System.out.println("getConceptOrders: " + data.toString());
            String[] salesOrgRows = data.get().split("\n");
            for (String row : salesOrgRows) {
                String[] parts = row.split("\t");
/*        if(parts.length == 2) {
          ConceptOrder salesOrg = new ConceptOrder(parts[0], parts[1]);
          salesOrgs.add(salesOrg);
        }*/
            }
        }
        return conceptOrders;
    }

    private void addSupportingOrders(ConceptOrder order) {
        ConceptOrder conceptOrder = new ConceptOrder();
        System.out.println("MainView addSupportingOrders..." + pageTitle);
        conceptOrder = new ConceptOrder();
        if ((pageTitle.equalsIgnoreCase("schema")) && (order.getConcept().equalsIgnoreCase("payment_vrd"))) {
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("Product");
            conceptOrder.setThetype("Entity");
            conceptOrder.setIinterface("IPayment");
            conceptOrder.setFunction("Pay");
            conceptOrder.setName("Payment");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("Double-Field");
            conceptOrder.setThetype("Property");
            conceptOrder.setIinterface("IPayment");
            conceptOrder.setFunction("Pay,Refund");
            conceptOrder.setName("payAmount");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("String-Field");
            conceptOrder.setThetype("Property");
            conceptOrder.setIinterface("IPayment");
            conceptOrder.setFunction("Pay,Refund");
            conceptOrder.setName("PayAccountId");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("Interface");
            conceptOrder.setThetype("Function");
            conceptOrder.setIinterface("boolean");
            conceptOrder.setFunction("string,double");
            conceptOrder.setName("Pay");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("Interface");
            conceptOrder.setThetype("Function");
            conceptOrder.setIinterface("boolean");
            conceptOrder.setFunction("string,double");
            conceptOrder.setName("Refund");
            conceptOrders.add(conceptOrder);
        }
        if ((pageTitle.equalsIgnoreCase("schema")) && (order.getConcept().equalsIgnoreCase("webitem_t4"))) {
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("Product");
            conceptOrder.setThetype("Entity");
            conceptOrder.setIinterface("IShop");
            conceptOrder.setFunction("ItemCRUD");
            conceptOrder.setName("webItem");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("Interface");
            conceptOrder.setThetype("Function");
            conceptOrder.setIinterface("boolean");
            conceptOrder.setFunction("string,string");
            conceptOrder.setName("ItemCRUD");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("String-Field");
            conceptOrder.setThetype("Property");
            conceptOrder.setIinterface("IShop");
            conceptOrder.setFunction("ItemCRUD");
            conceptOrder.setName("Item_Name");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("String-Field");
            conceptOrder.setThetype("Property");
            conceptOrder.setIinterface("IShop");
            conceptOrder.setFunction("ItemCRUD");
            conceptOrder.setName("Item_Description");
            conceptOrders.add(conceptOrder);
        }
        if ((pageTitle.equalsIgnoreCase("schema")) && (order.getConcept().equalsIgnoreCase("merchant_8ya"))) {
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("Product");
            conceptOrder.setThetype("Entity");
            conceptOrder.setIinterface("IMer");
            conceptOrder.setFunction("MerCRUD");
            conceptOrder.setName("Merchant");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("Interface");
            conceptOrder.setThetype("Function");
            conceptOrder.setIinterface("boolean");
            conceptOrder.setFunction("string,string");
            conceptOrder.setName("MerCRUD");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("String-Field");
            conceptOrder.setThetype("Property");
            conceptOrder.setIinterface("IMer");
            conceptOrder.setFunction("MerCRUD");
            conceptOrder.setName("Merchant_Code");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("String-Field");
            conceptOrder.setThetype("Property");
            conceptOrder.setIinterface("IMer");
            conceptOrder.setFunction("MerCRUD");
            conceptOrder.setName("Merchant_Account_No");
            conceptOrders.add(conceptOrder);
        }
        if ((pageTitle.equalsIgnoreCase("schema")) && (order.getConcept().equalsIgnoreCase("person_ln2"))) {
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("Product");
            conceptOrder.setThetype("Entity");
            conceptOrder.setIinterface("IPer");
            conceptOrder.setFunction("PerCRUD");
            conceptOrder.setName("Person");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("Interface");
            conceptOrder.setThetype("Function");
            conceptOrder.setIinterface("boolean");
            conceptOrder.setFunction("string,string");
            conceptOrder.setName("PerCRUD");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder = new ConceptOrder();
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("String-Field");
            conceptOrder.setThetype("Property");
            conceptOrder.setIinterface("IPer");
            conceptOrder.setFunction("PerCRUD");
            conceptOrder.setName("Person_First_Name");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("String-Field");
            conceptOrder.setThetype("Property");
            conceptOrder.setIinterface("IPer");
            conceptOrder.setFunction("PerCRUD");
            conceptOrder.setName("Person_Last_Name");
            conceptOrders.add(conceptOrder);
        }
        if ((pageTitle.equalsIgnoreCase("schema")) && (order.getConcept().equalsIgnoreCase("address_9za"))) {
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("Product");
            conceptOrder.setThetype("Entity");
            conceptOrder.setIinterface("IAddr");
            conceptOrder.setFunction("AddressCRUD");
            conceptOrder.setName("Address");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("Interface");
            conceptOrder.setThetype("Function");
            conceptOrder.setIinterface("boolean");
            conceptOrder.setFunction("string,string,string,string");
            conceptOrder.setName("AddressCRUD");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("String-Field");
            conceptOrder.setThetype("Property");
            conceptOrder.setIinterface("IAddr");
            conceptOrder.setFunction("AddressCRUD");
            conceptOrder.setName("Address_Street_Line");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("String-Field");
            conceptOrder.setThetype("Property");
            conceptOrder.setIinterface("IAddr");
            conceptOrder.setFunction("AddressCRUD");
            conceptOrder.setName("Address_Suburb");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("String-Field");
            conceptOrder.setThetype("Property");
            conceptOrder.setIinterface("IAddr");
            conceptOrder.setFunction("AddressCRUD");
            conceptOrder.setName("Address_City");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("String-Field");
            conceptOrder.setThetype("Property");
            conceptOrder.setIinterface("IAddr");
            conceptOrder.setFunction("AddressCRUD");
            conceptOrder.setName("Address_Country");
            conceptOrders.add(conceptOrder);
        }
        if ((pageTitle.equalsIgnoreCase("schema")) && (order.getConcept().equalsIgnoreCase("identity_j4w"))) {
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("Product");
            conceptOrder.setThetype("Entity");
            conceptOrder.setIinterface("Identity");
            conceptOrder.setFunction("IdentityCRUD");
            conceptOrder.setName("Identity");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("Interface");
            conceptOrder.setThetype("Function");
            conceptOrder.setIinterface("boolean");
            conceptOrder.setFunction("string,string,string,string");
            conceptOrder.setName("IdentityCRUD");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("String-Field");
            conceptOrder.setThetype("Property");
            conceptOrder.setIinterface("Identity");
            conceptOrder.setFunction("IdentityCRUD");
            conceptOrder.setName("ID_Number");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("String-Field");
            conceptOrder.setThetype("Property");
            conceptOrder.setIinterface("Identity");
            conceptOrder.setFunction("IdentityCRUD");
            conceptOrder.setName("Passport_Number");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("String-Field");
            conceptOrder.setThetype("Property");
            conceptOrder.setIinterface("Identity");
            conceptOrder.setFunction("IdentityCRUD");
            conceptOrder.setName("Driver_Licence_Number");
            conceptOrders.add(conceptOrder);
            conceptOrder = new ConceptOrder();
            conceptOrder.setTarget(order.getName());
            conceptOrder.setProcessed(false);
            conceptOrder.setConcept("String-Field");
            conceptOrder.setThetype("Property");
            conceptOrder.setIinterface("Identity");
            conceptOrder.setFunction("IdentityCRUD");
            conceptOrder.setName("Date_Of_Birth");
            conceptOrders.add(conceptOrder);
        }
    }

    public static VerticalLayout getInstance() {
        return (VerticalLayout) UI.getCurrent().getChildren().filter(component -> component.getId().get() == "").findFirst().orElse(null);
    }
}
