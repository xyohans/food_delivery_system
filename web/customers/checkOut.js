// checkOut.js — uses server-side cart (CartServlet)

let cart          = [];
let selectedItems = [];

// ── Load cart from server ────────────────────────────────────────────────────

async function loadCart() {
    try {
        const res  = await fetch('../CartServlet');
        if (res.status === 401) {
            window.location.href = '../index.html';
            return;
        }
        cart = await res.json();
        renderCart();
    } catch (err) {
        console.error('Failed to load cart', err);
    }
}

// ── Render cart items ────────────────────────────────────────────────────────

function renderCart() {
    const container = document.getElementById('cartContainer');
    container.innerHTML = '';

    if (cart.length === 0) {
        container.innerHTML = '<p style="padding:20px;color:#888">Your cart is empty. <a href="menu.html">Browse menu →</a></p>';
        document.getElementById('subtotal').textContent = 'Subtotal: $0.00';
        return;
    }

    let total = 0;
    cart.forEach(item => {
        const itemTotal = item.price * item.quantity;
        total += itemTotal;

        const div = document.createElement('div');
        div.innerHTML = `
            <input type="checkbox" class="selectItem" data-id="${item.itemId}">
            <span>${item.name} ($${item.price.toFixed(2)})</span>
            <input type="number" value="${item.quantity}" min="1" style="width:60px"
                onchange="updateQty(${item.itemId}, this.value)">
            <span>$${itemTotal.toFixed(2)}</span>
            <button onclick="checkoutSingle(${item.itemId})">Checkout</button>
            <button onclick="removeItem(${item.itemId})" style="background:#e74c3c">Remove</button>
        `;
        container.appendChild(div);
    });

    document.getElementById('subtotal').textContent = `Subtotal: $${total.toFixed(2)}`;
}

// ── Cart actions ─────────────────────────────────────────────────────────────

async function updateQty(itemId, qty) {
    const parsed = parseInt(qty) || 1;
    // Remove and re-add with new qty
    await fetch('../CartServlet', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({ action: 'remove', itemId })
    });
    const item = cart.find(i => i.itemId === itemId);
    if (item) {
        await fetch('CartServlet', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ action: 'add', itemId, name: item.name, price: item.price, quantity: parsed })
        });
    }
    await loadCart();
}

async function removeItem(itemId) {
    await fetch('CartServlet', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({ action: 'remove', itemId })
    });
    await loadCart();
}

// ── Checkout helpers ─────────────────────────────────────────────────────────

function checkoutSingle(itemId) {
    const item = cart.find(i => i.itemId === itemId);
    if (item) openPopup([item]);
}

function checkoutSelected() {
    const checked = document.querySelectorAll('.selectItem:checked');
    selectedItems = [];
    checked.forEach(cb => {
        const id   = parseInt(cb.dataset.id);
        const item = cart.find(i => i.itemId === id);
        if (item) selectedItems.push(item);
    });
    if (selectedItems.length === 0) { alert('Select items first'); return; }
    openPopup(selectedItems);
}

function checkoutAll() {
    if (cart.length === 0) { alert('Cart is empty'); return; }
    openPopup(cart);
}

// ── Order popup ──────────────────────────────────────────────────────────────

function openPopup(items) {
    selectedItems = items;
    const container = document.getElementById('orderItems');
    container.innerHTML = '';
    let total = 0;

    items.forEach(item => {
        const itemTotal = item.price * item.quantity;
        total += itemTotal;
        const div = document.createElement('div');
        div.textContent = `${item.name} ×${item.quantity} = $${itemTotal.toFixed(2)}`;
        container.appendChild(div);
    });

    document.getElementById('finalTotal').textContent = `Total: $${total.toFixed(2)}`;
    loadAddresses();
    document.getElementById('orderPopup').style.display = 'flex';
}

function closePopup() {
    document.getElementById('orderPopup').style.display = 'none';
}

// ── Addresses ────────────────────────────────────────────────────────────────

async function loadAddresses() {
    try {
        const res       = await fetch('../LoadAddressServlet');
        const addresses = await res.json();
        const select    = document.getElementById('addressSelect');
        select.innerHTML = '';

        if (addresses.length === 0) {
            const opt = document.createElement('option');
            opt.value = ''; opt.textContent = '— No saved addresses —';
            select.appendChild(opt);
            return;
        }

        addresses.forEach(addr => {
            const opt = document.createElement('option');
            opt.value = addr.id;
            opt.textContent = `${addr.label} — ${addr.address}`;
            if (addr.is_default) opt.selected = true;
            select.appendChild(opt);
        });
    } catch (err) {
        console.error('Failed to load addresses', err);
    }
}

// ── Place order ──────────────────────────────────────────────────────────────

async function placeOrder() {
    const addressId = document.getElementById('addressSelect').value;
    const payment   = document.getElementById('paymentSelect').value;
    const notes     = document.getElementById('notes').value;

    if (!addressId) {
        alert('Please add a delivery address first');
        return;
    }

    const body = {
        items:          selectedItems.map(i => ({ item_id: i.itemId, quantity: i.quantity })),
        address_id:     parseInt(addressId),
        payment_method: payment,
        notes:          notes
    };

    try {
        const res  = await fetch('../PlaceOrderServlet', {
            method:  'POST',
            headers: {'Content-Type': 'application/json'},
            body:    JSON.stringify(body)
        });
        const data = await res.json();

        if (res.ok) {
            alert(data.message);
            // Remove ordered items from cart
            for (const item of selectedItems) {
                await fetch('CartServlet', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({ action: 'remove', itemId: item.itemId })
                });
            }
            closePopup();
            await loadCart();
            loadBalance();
        } else {
            alert('Error: ' + (data.error || 'Order failed'));
        }
    } catch (err) {
        console.error(err);
        alert('Network error. Please try again.');
    }
}

// ── Balance ──────────────────────────────────────────────────────────────────

async function loadBalance() {
    try {
        const res  = await fetch('../AccountServlet');
        const data = await res.json();
        if (data.balance !== undefined) {
            const el = document.getElementById('nav-balance');
            if (el) el.textContent = `Balance: $${data.balance.toFixed(2)}`;
        }
    } catch (err) {}
}

function logout() {
    window.location.href = '../LogoutServlet';
}

// ── Init ─────────────────────────────────────────────────────────────────────
loadCart();
loadBalance();
