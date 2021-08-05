package com.vaadin.tutorial;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.Route;
import com.vaadin.resource.ConceptOrder;

import java.util.*;
import java.util.stream.Collectors;

@Route("api")
public class ApiView extends VerticalLayout {
	public static List<ConceptOrder> apiOrders = new ArrayList<>();
	public ApiView() {
		//apiOrders = new MainView().conceptOrders;
		System.out.println("ApiView...");
		// HEADER
		Comparator<ConceptOrder> compareByConcept = (ConceptOrder o1, ConceptOrder o2) -> o1.getConcept().compareTo( o2.getConcept() );
		Collections.sort(apiOrders, compareByConcept);
		Icon drawer = VaadinIcon.MENU.create();
		Span title = new Span("Enterprise Service Boutique API");
		Icon help = VaadinIcon.QUESTION_CIRCLE.create();
		HorizontalLayout header = new HorizontalLayout(drawer, title, help);
		header.expand(title);
		header.setPadding(true);
		header.setWidth("100%");

		// WORKSPACE
		TreeGrid<ConceptOrder> treeGrid = new TreeGrid<>(ConceptOrder.class);
		treeGrid.setHierarchyColumn("name");
		for (ConceptOrder temp : apiOrders) {
			System.out.println("Current Item: " + temp.getName());
			List<ConceptOrder> resultList = new ArrayList<ConceptOrder>(apiOrders);
			List<ConceptOrder> childResult = resultList.stream().filter(p -> p.getTarget().equalsIgnoreCase(temp.getName())).collect(Collectors.toList());
			List<ConceptOrder> parentResult = resultList.stream().filter(p -> p.getName().equalsIgnoreCase(temp.getTarget())).collect(Collectors.toList());
			if((parentResult == null) ||(parentResult.size() <= 0)){
				if(!treeGrid.getTreeData().contains(temp)) {
					treeGrid.getTreeData().addItem(null, temp);
					System.out.println("Aparent NULL insert |child| " + temp.getName());
				}else{
					treeGrid.getTreeData().setParent(temp, null);
					System.out.println("Aparent NULL update |child| " + temp.getName());
				}
				if((childResult != null) && (childResult.size() > 0)) {
					System.out.println("ACurrent Item: " + temp.getName() + " size:" + childResult.size());
					List<ConceptOrder> childrenConceptOrder = childResult;
					for(ConceptOrder co: childrenConceptOrder) {
						System.out.println("Achild1 "  + co.getName() + " |parent| " + temp.getName());
						if (!treeGrid.getTreeData().contains(co)) {
							treeGrid.getTreeData().addItem(temp, co);
							System.out.println("Achild2 insert"  + co.getName() + " |parent| " + temp.getName());
						}else{
							treeGrid.getTreeData().setParent(co, temp);
							System.out.println("Achild2 update"  + co.getName() + " |parent| " + temp.getName());
						}
					}
				}
			}else{
				ConceptOrder parentConceptOrder = parentResult.get(0);
				if(!treeGrid.getTreeData().contains(temp)) {
					if(!treeGrid.getTreeData().contains(parentConceptOrder)) {
						treeGrid.getTreeData().addItem(null, parentConceptOrder);
						System.out.println("Bparent NULL insert |child| " + parentConceptOrder.getName());
					}else{
						treeGrid.getTreeData().setParent(parentConceptOrder, null);
						System.out.println("Bparent NULL update |child| " + parentConceptOrder.getName());
					}
					treeGrid.getTreeData().addItem(parentConceptOrder, temp);
					System.out.println("Bparent " + parentConceptOrder.getName() + " |child| " + temp.getName());
				}
				if((childResult != null) && (childResult.size() > 0)) {
					System.out.println("BCurrent Item: " + temp.getName() + " size:" + childResult.size());
					List<ConceptOrder> childrenConceptOrder = childResult;
					for(ConceptOrder co: childrenConceptOrder) {
						System.out.println("Bchild1 " + co.getName() + " |parent| " + temp.getName());
						if (!treeGrid.getTreeData().contains(co)) {
							treeGrid.getTreeData().addItem(temp, co);
							System.out.println("Bchild2 insert: " + co.getName() + " |parent| " + temp.getName());
						}else{
							treeGrid.getTreeData().setParent(co, temp);
							System.out.println("Bchild2 update: " + co.getName() + " |parent| " + temp.getName());
						}
					}
				}
			}
			//System.out.println(temp);
		}
		VerticalLayout workspace = new VerticalLayout((Component) treeGrid);
		workspace.setSizeFull();

		// FOOTER
		Tab actionButton1 = new Tab(VaadinIcon.HOME.create(), new Span("Home"));
		Tab actionButton2 = new Tab(VaadinIcon.USERS.create(), new Span("Customers"));
		Tab actionButton3 = new Tab(VaadinIcon.PACKAGE.create(), new Span("Products"));
		Tabs buttonBar = new Tabs(actionButton1, actionButton2, actionButton3);
		HorizontalLayout footer = new HorizontalLayout(buttonBar);
		footer.setJustifyContentMode(JustifyContentMode.CENTER);
		footer.setWidth("100%");

		// MAIN CONTAINER
		setSizeFull();
		setMargin(false);
		setSpacing(false);
		setPadding(false);
		add(header, workspace, footer);
	}

	private List<ConceptOrder> buildList() {
		List<ConceptOrder> localOrders = new ArrayList<>();
		String wawa = "";
		boolean processCompleted = false;
		while(!processCompleted) {
			boolean productFound = false;
			int countProcessed = 0;
			int instanceCount = 0;
			for (ConceptOrder co : apiOrders) {
				String name = co.getName().trim().toLowerCase(Locale.ROOT);
				String concept = co.getConcept().trim().toLowerCase(Locale.ROOT);
				String thetype = co.getThetype().trim().toLowerCase(Locale.ROOT);
				String target = co.getTarget().trim().toLowerCase(Locale.ROOT);
				boolean processed = co.getProcessed();
				System.out.println("ApiView..." + name + " type: " + thetype + " concept: " + concept);
				if (thetype.equalsIgnoreCase("object")) {
					countProcessed += 1;
					localOrders.add(co);
					List<ConceptOrder> resultList = new ArrayList<ConceptOrder>(apiOrders);
					List<ConceptOrder> result = resultList
							.stream()
							.filter(p -> p.getTarget().equalsIgnoreCase(target))
							.collect(Collectors.toList());
					//CollectionUtils.filter(result, o -> ((ConceptOrder) o).getTarget().equalsIgnoreCase(target));
					instanceCount = result.size();
					System.out.println("product: " + name + " count: " + instanceCount);
					wawa += "\nproduct: " + name;
					productFound = true;
					if (countProcessed >= instanceCount) {
						processCompleted = true;
					}
				}
				if (productFound) {
					if (thetype.equalsIgnoreCase("property")) {
						countProcessed += 1;
						localOrders.add(co);
						System.out.println("property: " + name + " processed: " + countProcessed);
						wawa += "\nproperty: " + name;
						if (countProcessed >= instanceCount) {
							processCompleted = true;
						}
					}
					if (thetype.equalsIgnoreCase("dimension")) {
						countProcessed += 1;
						localOrders.add(co);
						System.out.println("dimension: " + name + " processed: " + countProcessed);
						wawa += "\ndimension: " + name;
						if (countProcessed >= instanceCount) {
							processCompleted = true;
						}
					}
				}
			}
		}

		return localOrders;
	}
}