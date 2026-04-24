-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 24, 2026 at 11:45 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `food_delivery`
--

-- --------------------------------------------------------

--
-- Table structure for table `addresses`
--

CREATE TABLE `addresses` (
  `id` int(10) UNSIGNED NOT NULL,
  `user_id` int(10) UNSIGNED NOT NULL,
  `label` varchar(50) NOT NULL DEFAULT 'Home',
  `address` text NOT NULL,
  `is_default` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `cancellations`
--

CREATE TABLE `cancellations` (
  `id` int(10) UNSIGNED NOT NULL,
  `order_id` int(10) UNSIGNED NOT NULL,
  `cancelled_by` int(10) UNSIGNED NOT NULL,
  `reason` text DEFAULT NULL,
  `cancelled_at` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `menu_categories`
--

CREATE TABLE `menu_categories` (
  `id` int(10) UNSIGNED NOT NULL,
  `name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `menu_categories`
--

INSERT INTO `menu_categories` (`id`, `name`) VALUES
(1, 'Burgers'),
(2, 'Pizza'),
(3, 'Pasta'),
(4, 'Grills'),
(5, 'Sandwiches'),
(6, 'Salads'),
(7, 'Soups'),
(8, 'Sides'),
(9, 'Desserts'),
(10, 'Hot Drinks'),
(11, 'Cold Drinks'),
(12, 'Juices');

-- --------------------------------------------------------

--
-- Table structure for table `menu_items`
--

CREATE TABLE `menu_items` (
  `id` int(10) UNSIGNED NOT NULL,
  `category_id` int(10) UNSIGNED DEFAULT NULL,
  `name` varchar(150) NOT NULL,
  `description` text DEFAULT NULL,
  `price` decimal(8,2) NOT NULL,
  `is_available` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `menu_items`
--

INSERT INTO `menu_items` (`id`, `category_id`, `name`, `description`, `price`, `is_available`) VALUES
(1, 1, 'Classic Burger', 'Beef patty, lettuce, tomato, pickles, ketchup and mustard', 5.99, 1),
(2, 1, 'Cheese Burger', 'Beef patty with melted cheddar cheese, lettuce, tomato and special sauce', 6.99, 1),
(3, 1, 'Double Burger', 'Two beef patties, double cheese, lettuce, tomato and caramelized onions', 8.99, 1),
(4, 1, 'Crispy Chicken Burger', 'Crispy fried chicken fillet, coleslaw, pickles and mayo', 7.49, 1),
(5, 1, 'Spicy Burger', 'Beef patty with jalapeños, pepper jack cheese, sriracha mayo and onion rings', 7.99, 1),
(6, 1, 'Mushroom Burger', 'Beef patty topped with sautéed mushrooms, Swiss cheese and garlic aioli', 7.99, 1),
(7, 1, 'BBQ Bacon Burger', 'Beef patty, crispy bacon, BBQ sauce, cheddar cheese and crispy onions', 9.49, 1),
(8, 1, 'Veggie Burger', 'Plant-based patty, avocado, lettuce, tomato and vegan mayo', 7.49, 1),
(9, 2, 'Margherita', 'Classic tomato sauce, fresh mozzarella and basil', 8.99, 1),
(10, 2, 'Pepperoni', 'Tomato sauce, mozzarella and generous pepperoni slices', 10.99, 1),
(11, 2, 'BBQ Chicken Pizza', 'BBQ sauce, grilled chicken, red onions, mozzarella and fresh cilantro', 12.49, 1),
(12, 2, 'Four Cheese', 'Mozzarella, cheddar, parmesan and gorgonzola on tomato sauce', 12.99, 1),
(13, 2, 'Veggie Supreme', 'Tomato sauce, mozzarella, bell peppers, mushrooms, olives and red onions', 11.49, 1),
(14, 2, 'Meat Feast', 'Pepperoni, beef, chicken, bacon and mozzarella on rich tomato sauce', 13.99, 1),
(15, 2, 'Hawaiian', 'Tomato sauce, mozzarella, ham and pineapple', 10.99, 1),
(16, 2, 'Spicy Beef Pizza', 'Tomato sauce, spiced minced beef, jalapeños, mozzarella and chili flakes', 12.49, 1),
(17, 3, 'Spaghetti Bolognese', 'Spaghetti with slow-cooked beef and tomato ragù, topped with parmesan', 9.99, 1),
(18, 3, 'Penne Arrabiata', 'Penne in a spicy tomato and garlic sauce with fresh basil', 8.49, 1),
(19, 3, 'Fettuccine Alfredo', 'Fettuccine in a rich creamy parmesan sauce', 9.49, 1),
(20, 3, 'Chicken Pesto Pasta', 'Penne with grilled chicken, basil pesto, cherry tomatoes and parmesan', 10.99, 1),
(21, 3, 'Seafood Pasta', 'Linguine with shrimp, calamari and mussels in a white wine garlic sauce', 13.99, 1),
(22, 3, 'Mac and Cheese', 'Elbow pasta in a creamy four-cheese sauce, topped with breadcrumbs', 7.99, 1),
(23, 4, 'Grilled Chicken', 'Marinated whole grilled chicken served with garlic bread and coleslaw', 12.99, 1),
(24, 4, 'Mixed Grill Platter', 'Chicken, beef kofta and lamb chops served with rice, salad and sauce', 18.99, 1),
(25, 4, 'Beef Kofta', 'Spiced minced beef skewers grilled over charcoal, served with rice and salad', 11.99, 1),
(26, 4, 'Lamb Chops', 'Marinated lamb chops grilled to perfection, served with grilled vegetables', 16.99, 1),
(27, 4, 'Grilled Salmon', 'Fresh salmon fillet with lemon butter sauce, served with steamed vegetables', 15.99, 1),
(28, 4, 'Shish Tawook', 'Marinated chicken cubes grilled on skewers, served with garlic sauce and rice', 11.49, 1),
(29, 5, 'Club Sandwich', 'Grilled chicken, bacon, egg, lettuce, tomato and mayo in toasted bread', 7.49, 1),
(30, 5, 'Falafel Wrap', 'Crispy falafel, hummus, tomato, cucumber and tahini in a warm wrap', 5.99, 1),
(31, 5, 'Shawarma Wrap', 'Seasoned chicken or beef with garlic sauce, pickles and fries in a wrap', 6.99, 1),
(32, 5, 'Tuna Melt', 'Tuna salad with melted cheddar on toasted sourdough', 6.49, 1),
(33, 5, 'BLT Sandwich', 'Crispy bacon, lettuce and tomato with mayo on toasted white bread', 5.99, 1),
(34, 5, 'Grilled Veggie Wrap', 'Grilled zucchini, peppers, hummus, feta and rocket in a whole wheat wrap', 5.49, 1),
(35, 6, 'Caesar Salad', 'Romaine lettuce, croutons, parmesan and caesar dressing', 6.99, 1),
(36, 6, 'Greek Salad', 'Tomatoes, cucumber, olives, red onion, feta cheese and olive oil', 6.49, 1),
(37, 6, 'Chicken Caesar Salad', 'Grilled chicken strips on romaine with caesar dressing and croutons', 8.99, 1),
(38, 6, 'Fattoush', 'Mixed greens, tomato, cucumber, radish, crispy bread chips and pomegranate dressing', 5.99, 1),
(39, 6, 'Quinoa Salad', 'Quinoa, avocado, cherry tomatoes, cucumber, feta and lemon vinaigrette', 8.49, 1),
(40, 7, 'Tomato Soup', 'Creamy roasted tomato soup served with crusty bread', 4.99, 1),
(41, 7, 'Lentil Soup', 'Traditional red lentil soup with cumin and lemon, served with bread', 4.49, 1),
(42, 7, 'Chicken Soup', 'Hearty chicken broth with vegetables and noodles', 5.49, 1),
(43, 7, 'Mushroom Soup', 'Creamy wild mushroom soup with a drizzle of truffle oil', 5.99, 1),
(44, 8, 'French Fries', 'Crispy golden fries seasoned with sea salt', 2.99, 1),
(45, 8, 'Loaded Fries', 'Fries topped with cheese sauce, bacon bits and jalapeños', 4.99, 1),
(46, 8, 'Onion Rings', 'Crispy battered onion rings served with dipping sauce', 3.49, 1),
(47, 8, 'Coleslaw', 'Creamy homemade coleslaw', 1.99, 1),
(48, 8, 'Garlic Bread', 'Toasted bread with garlic butter and herbs', 2.49, 1),
(49, 8, 'Rice', 'Steamed white or brown rice', 1.99, 1),
(50, 8, 'Sweet Potato Fries', 'Oven-baked sweet potato fries with honey mustard dip', 3.49, 1),
(51, 8, 'Hummus with Bread', 'Smooth hummus drizzled with olive oil served with warm pita bread', 3.99, 1),
(52, 9, 'Chocolate Lava Cake', 'Warm chocolate cake with a molten center served with vanilla ice cream', 5.99, 1),
(53, 9, 'Cheesecake', 'Classic New York cheesecake with a graham cracker crust and berry compote', 5.49, 1),
(54, 9, 'Tiramisu', 'Italian classic with espresso-soaked ladyfingers and mascarpone cream', 5.49, 1),
(55, 9, 'Kunafa', 'Crispy shredded pastry with sweet cheese filling soaked in rose syrup', 4.99, 1),
(56, 9, 'Baklava', 'Layers of flaky pastry with crushed pistachios and honey syrup', 3.99, 1),
(57, 9, 'Ice Cream (2 Scoops)', 'Choice of vanilla, chocolate or strawberry', 3.49, 1),
(58, 9, 'Brownie', 'Fudgy chocolate brownie served warm with ice cream', 4.49, 1),
(59, 9, 'Crème Brûlée', 'Classic French custard with a caramelized sugar crust', 5.99, 1),
(60, 10, 'Espresso', 'Single shot of rich Italian espresso', 2.49, 1),
(61, 10, 'Americano', 'Espresso with hot water, smooth and bold', 2.99, 1),
(62, 10, 'Cappuccino', 'Espresso with steamed milk and a thick layer of foam', 3.49, 1),
(63, 10, 'Latte', 'Espresso with lots of steamed milk and a light foam', 3.49, 1),
(64, 10, 'Flat White', 'Double espresso with microfoam milk', 3.49, 1),
(65, 10, 'Mocha', 'Espresso with chocolate syrup and steamed milk', 3.99, 1),
(66, 10, 'Turkish Coffee', 'Finely ground coffee brewed in a cezve, served with Turkish delight', 2.99, 1),
(67, 10, 'Hot Chocolate', 'Rich creamy hot chocolate topped with whipped cream', 3.49, 1),
(68, 10, 'Mint Tea', 'Fresh mint leaves steeped in hot water', 2.49, 1),
(69, 10, 'Chamomile Tea', 'Soothing chamomile flower tea', 2.49, 1),
(70, 11, 'Iced Latte', 'Espresso poured over ice with cold milk', 3.99, 1),
(71, 11, 'Iced Americano', 'Espresso over ice with cold water', 3.49, 1),
(72, 11, 'Frappuccino', 'Blended coffee with ice, milk and whipped cream', 4.49, 1),
(73, 11, 'Iced Matcha Latte', 'Ceremonial matcha blended with cold milk over ice', 4.49, 1),
(74, 11, 'Soft Drink (Can)', 'Pepsi, 7UP, Mirinda or water — please specify', 1.49, 1),
(75, 11, 'Sparkling Water', 'Chilled sparkling mineral water', 1.99, 1),
(76, 11, 'Milkshake', 'Thick shake in vanilla, chocolate or strawberry', 4.99, 1),
(77, 11, 'Lemonade', 'Freshly squeezed lemonade with mint', 3.49, 1),
(78, 12, 'Orange Juice', 'Freshly squeezed orange juice', 3.49, 1),
(79, 12, 'Mango Juice', 'Fresh blended mango juice', 3.49, 1),
(80, 12, 'Watermelon Juice', 'Cold fresh watermelon juice', 3.49, 1),
(81, 12, 'Mixed Fruit Juice', 'Seasonal blend of fresh fruits', 3.99, 1),
(82, 12, 'Carrot Juice', 'Fresh carrot with a hint of ginger and lemon', 3.49, 1),
(83, 12, 'Avocado Smoothie', 'Creamy blended avocado with milk and a touch of honey', 4.49, 1),
(84, 12, 'Strawberry Smoothie', 'Fresh strawberries blended with yogurt and honey', 4.49, 1),
(85, 12, 'Green Detox Juice', 'Spinach, cucumber, apple, lemon and ginger', 4.49, 1);

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `id` int(10) UNSIGNED NOT NULL,
  `user_id` int(10) UNSIGNED NOT NULL,
  `order_id` int(10) UNSIGNED DEFAULT NULL,
  `message` text NOT NULL,
  `is_read` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `id` int(10) UNSIGNED NOT NULL,
  `customer_id` int(10) UNSIGNED NOT NULL,
  `chef_id` int(10) UNSIGNED DEFAULT NULL,
  `delivery_id` int(10) UNSIGNED DEFAULT NULL,
  `address_id` int(10) UNSIGNED DEFAULT NULL,
  `status` enum('pending','paid','preparing','ready','delivering','delivered','cancelled') NOT NULL DEFAULT 'pending',
  `delivery_address` text NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `notes` text DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `order_items`
--

CREATE TABLE `order_items` (
  `id` int(10) UNSIGNED NOT NULL,
  `order_id` int(10) UNSIGNED NOT NULL,
  `item_id` int(10) UNSIGNED NOT NULL,
  `quantity` tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
  `unit_price` decimal(8,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

CREATE TABLE `payments` (
  `id` int(10) UNSIGNED NOT NULL,
  `order_id` int(10) UNSIGNED NOT NULL,
  `method` enum('card','cash','wallet') NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `status` enum('pending','completed','failed','refunded') NOT NULL DEFAULT 'pending',
  `paid_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(10) UNSIGNED NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `role` enum('customer','admin','chef','delivery') NOT NULL,
  `balance` decimal(10,2) NOT NULL DEFAULT 0.00,
  `is_free` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `password`, `phone`, `role`, `balance`, `is_free`, `created_at`) VALUES
(1, '', 'yohannisabraham13@gmail.com', '123', NULL, '', 0.00, 1, '2026-04-22 22:58:52');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `addresses`
--
ALTER TABLE `addresses`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_addresses_user` (`user_id`);

--
-- Indexes for table `cancellations`
--
ALTER TABLE `cancellations`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_cancellations_order` (`order_id`),
  ADD KEY `fk_cancellations_user` (`cancelled_by`);

--
-- Indexes for table `menu_categories`
--
ALTER TABLE `menu_categories`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `menu_items`
--
ALTER TABLE `menu_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_items_category` (`category_id`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_notifications_user` (`user_id`),
  ADD KEY `fk_notifications_order` (`order_id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_orders_customer` (`customer_id`),
  ADD KEY `fk_orders_chef` (`chef_id`),
  ADD KEY `fk_orders_delivery` (`delivery_id`),
  ADD KEY `fk_orders_address` (`address_id`);

--
-- Indexes for table `order_items`
--
ALTER TABLE `order_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_order_items_order` (`order_id`),
  ADD KEY `fk_order_items_item` (`item_id`);

--
-- Indexes for table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_payments_order` (`order_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_users_email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `addresses`
--
ALTER TABLE `addresses`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `cancellations`
--
ALTER TABLE `cancellations`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `menu_categories`
--
ALTER TABLE `menu_categories`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `menu_items`
--
ALTER TABLE `menu_items`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=86;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `order_items`
--
ALTER TABLE `order_items`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `addresses`
--
ALTER TABLE `addresses`
  ADD CONSTRAINT `fk_addresses_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `cancellations`
--
ALTER TABLE `cancellations`
  ADD CONSTRAINT `fk_cancellations_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_cancellations_user` FOREIGN KEY (`cancelled_by`) REFERENCES `users` (`id`) ON UPDATE CASCADE;

--
-- Constraints for table `menu_items`
--
ALTER TABLE `menu_items`
  ADD CONSTRAINT `fk_items_category` FOREIGN KEY (`category_id`) REFERENCES `menu_categories` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `fk_notifications_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_notifications_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `fk_orders_address` FOREIGN KEY (`address_id`) REFERENCES `addresses` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_orders_chef` FOREIGN KEY (`chef_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_orders_customer` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_orders_delivery` FOREIGN KEY (`delivery_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `order_items`
--
ALTER TABLE `order_items`
  ADD CONSTRAINT `fk_order_items_item` FOREIGN KEY (`item_id`) REFERENCES `menu_items` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_order_items_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `fk_payments_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
