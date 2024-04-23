package com.chainsys.jfs.Stock;

import java.util.List;
import java.util.Scanner;

public class StockManagementInformation {
	 public void addProductDetails(Product product) {
	        Scanner scanner = new Scanner(System.in);
	        System.out.println("\nEnter Product Details:");
	        System.out.print("Product Name: ");
	        product.setProductName(scanner.nextLine());
	        System.out.print("Product ID: ");
	        product.setProductId(scanner.nextInt());
	        System.out.print("Quantity: ");
	        product.setQuantity(scanner.nextInt());
	    }

	    public void displayProductDetails(List<Product> products) {
	        if (products.isEmpty()) {
	            System.out.println("\nNo products available!");
	        } else {
	            System.out.println("\nProduct Details:");
	            for (Product product : products) {
	                System.out.println("Product Name: " + product.getProductName());
	                System.out.println("Product ID: " + product.getProductId());
	                System.out.println("Quantity: " + product.getQuantity());
	                System.out.println("-------------------------------------");
	            }
	        }
	    }

	    public void checkStockAvailability(List<Product> products) {
	        if (products.isEmpty()) {
	            System.out.println("\nNo products available!");
	        } else {
	            System.out.println("\nStock Availability:");
	            for (Product product : products) {
	                System.out.println("Product Name: " + product.getProductName());
	                if (product.getQuantity() > 0) {
	                    System.out.println("Available in stock.");
	                } else {
	                    System.out.println("Out of stock.");
	                }
	                System.out.println("-------------------------------------");
	            }
	        }
	    }
}
