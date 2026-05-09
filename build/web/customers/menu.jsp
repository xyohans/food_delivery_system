
<html>
    <head>
        <title>Food Menu</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link  href="menuStyle.css" rel="stylesheet"/>
    </head>

    <body>
        <div class="filter"></div>
        <div class="menus"></div>
        <div id="popup" class="popup">
            <div class="popup-content">
                <span class="close-btn" onclick="closePopup()">×</span>

                <p class="name"></p>
                <label> quantity: <input type="number" class="quantity" /></label> 
                <p class="price"></p>
                <p id="totalPrice">Total: </p>   
                <select id="addressSelect"></select>


                <button onclick="openAddressPopup()">Add Address</button>

                <select id="paymentSelect">
                    <option value="cash">Cash</option>
                    <option value="card">Card</option>
                    <option value="wallet">Wallet</option>
                </select>

                <textarea placeholder="do u what anything to be chage to the menu..."></textarea>
                <button onclick="sendOrder()">Place Order</button>
            </div>
        </div>
        <div id="addressPopup" class="popup">
            <div class="popup-content">
                <span class="close-btn" onclick="closeAddressPopup()">×</span>

                <h3>Add New Address</h3>

                <input type="text" id="labelInput" placeholder="Label (Home, Work...)" />

                <input type="text" id="addressInput" placeholder="Full Address" />

                <label>
                    <input type="checkbox" id="defaultCheck" />
                    Set as default
                </label>

                <button onclick="submitAddress()">Save Address</button>
            </div>
        </div>
        <div id="successPopup" class="popup">
            <div class="popup-content">
                <span class="close-btn" onclick="closeSuccessPopup()">×</span>

                <h3>Order Status</h3>
                <p id="successMessage"></p>

                <button onclick="closeSuccessPopup()">OK</button>
            </div>
        </div>
        <script>
            const userId = "<%= session.getAttribute("userId")%>";
        </script>
        <script src="menu.js">
        </script>
    </body>
</html>
