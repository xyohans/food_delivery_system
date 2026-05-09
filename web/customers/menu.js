// menu.js

let data               = [];
let selectedCategories = new Set();
let selectedItem       = null;

// ── Popup helpers ────────────────────────────────────────────────────────────

function openSuccessPopup(message) {
    document.getElementById('successMessage').textContent = message;
    document.getElementById('successPopup').style.display = 'flex';
}
function closeSuccessPopup() {
    document.getElementById('successPopup').style.display = 'none';
}

function openPopup(item) {
    loadAddresses();
    selectedItem = item;
    document.querySelector('.name').textContent  = item.name;
    document.querySelector('.price').textContent = `Price: $${item.price.toFixed(2)}`;
    const qty = document.querySelector('.quantity');
    qty.value = 1;
    document.getElementById('totalPrice').textContent = `Total: $${item.price.toFixed(2)}`;
    document.getElementById('popup').style.display = 'flex';
}
function closePopup() {
    document.getElementById('popup').style.display = 'none';
}

function openAddressPopup() {
    document.getElementById('addressPopup').style.display = 'flex';
}
function closeAddressPopup() {
    document.getElementById('addressPopup').style.display = 'none';
}

// ── Quantity input listener (safe — only updates when an item is selected) ───

document.querySelector('.quantity').addEventListener('input', function () {
    if (!selectedItem) return;
    const qty          = parseInt(this.value) || 1;
    const displayTotal = (selectedItem.price * qty).toFixed(2);
    document.getElementById('totalPrice').textContent = `Total: $${displayTotal}`;
});

// ── Rendering ────────────────────────────────────────────────────────────────

const menus            = document.querySelector('.menus');
const filtersContainer = document.querySelector('.filter');

function renderItems(items) {
    menus.innerHTML = '';
    const filtered = selectedCategories.size === 0
        ? items
        : items.filter(item => selectedCategories.has(item.categoryName));

    if (filtered.length === 0) {
        menus.innerHTML = '<p style="padding:20px;color:#888">No items found.</p>';
        return;
    }

    filtered.forEach(item => {
        const container = document.createElement('div');

        const nameEl = document.createElement('p');
        nameEl.textContent = item.name;

        const priceEl = document.createElement('span');
        priceEl.textContent = `$${item.price.toFixed(2)}`;

        const desc = document.createElement('p');
        desc.textContent = item.description || '';
        desc.style.cssText = 'color:#666;font-size:0.85rem;flex:1';

        const btnOrder = document.createElement('button');
        btnOrder.textContent = 'Order Now';
        btnOrder.addEventListener('click', () => openPopup(item));

        const btnCart = document.createElement('button');
        btnCart.textContent = 'Add to Cart';
        btnCart.addEventListener('click', () => addToCart(item));

        container.append(nameEl, priceEl, desc, btnOrder, btnCart);
        menus.appendChild(container);
    });
}

function buildFilters(items) {
    const categories = [...new Set(items.map(item => item.categoryName))];
    filtersContainer.innerHTML = '<h3 style="margin-bottom:10px">Categories</h3>';

    // "All" toggle
    const allLabel = document.createElement('label');
    const allCb    = document.createElement('input');
    allCb.type = 'checkbox'; allCb.id = 'filter-all';
    allLabel.append(allCb, ' All');
    allCb.addEventListener('change', () => {
        filtersContainer.querySelectorAll('input[data-cat]').forEach(cb => {
            cb.checked = allCb.checked;
            if (allCb.checked) selectedCategories.add(cb.value);
            else               selectedCategories.delete(cb.value);
        });
        renderItems(data);
    });
    filtersContainer.appendChild(allLabel);

    categories.forEach(cat => {
        const label = document.createElement('label');
        const cb    = document.createElement('input');
        cb.type  = 'checkbox';
        cb.value = cat;
        cb.setAttribute('data-cat', '1');
        label.append(cb, ' ' + cat);
        filtersContainer.appendChild(label);
        cb.addEventListener('change', () => {
            if (cb.checked) selectedCategories.add(cat);
            else            selectedCategories.delete(cat);
            renderItems(data);
        });
    });
}

// ── Cart ─────────────────────────────────────────────────────────────────────

async function addToCart(item) {
    try {
        const res = await fetch('../CartServlet', {
            method:  'POST',
            headers: {'Content-Type': 'application/json'},
            body:    JSON.stringify({
                action:   'add',
                itemId:   item.id,
                name:     item.name,
                price:    item.price,
                quantity: 1
            })
        });
        if (res.ok) {
            showToast(`${item.name} added to cart!`);
        }
    } catch (err) {
        console.error('Cart error', err);
    }
}

function showToast(msg) {
    let t = document.getElementById('toast');
    if (!t) {
        t = document.createElement('div');
        t.id = 'toast';
        t.style.cssText = `position:fixed;bottom:30px;right:30px;background:#2f3542;
            color:white;padding:12px 20px;border-radius:10px;font-size:0.9rem;
            z-index:9999;opacity:0;transition:opacity 0.3s`;
        document.body.appendChild(t);
    }
    t.textContent = msg;
    t.style.opacity = '1';
    setTimeout(() => { t.style.opacity = '0'; }, 2500);
}

// ── Place single-item order ───────────────────────────────────────────────────

async function sendOrder() {
    const qty           = parseInt(document.querySelector('.quantity').value) || 1;
    const addressId     = document.querySelector('#addressSelect').value;
    const paymentMethod = document.querySelector('#paymentSelect').value;
    const notes         = document.querySelector('textarea').value;

    if (!addressId) {
        alert('Please select or add a delivery address first.');
        return;
    }

    const body = {
        items:          [{ item_id: selectedItem.id, quantity: qty }],
        address_id:     parseInt(addressId),
        payment_method: paymentMethod,
        notes:          notes
    };

    try {
        const res  = await fetch('../PlaceOrderServlet', {
            method:  'POST',
            headers: {'Content-Type': 'application/json'},
            body:    JSON.stringify(body)
        });
        const d = await res.json();

        closePopup();
        if (res.ok) {
            openSuccessPopup(d.message || 'Order placed successfully!');
            loadBalance();
        } else {
            openSuccessPopup('Error: ' + (d.error || 'Order failed'));
        }
    } catch (err) {
        console.error(err);
        openSuccessPopup('Something went wrong while placing order.');
    }
}

// ── Addresses ────────────────────────────────────────────────────────────────

async function loadAddresses() {
    try {
        const res       = await fetch('../LoadAddressServlet');
        const addresses = await res.json();
        const select    = document.querySelector('#addressSelect');
        select.innerHTML = '';

        if (addresses.length === 0) {
            const opt = document.createElement('option');
            opt.value = ''; opt.textContent = '— No addresses saved —';
            select.appendChild(opt);
            return;
        }

        let defaultFound = false;
        addresses.forEach(addr => {
            const opt       = document.createElement('option');
            opt.value       = addr.id;
            opt.textContent = `${addr.label} — ${addr.address}`;
            if (addr.is_default && !defaultFound) {
                opt.selected = true;
                defaultFound = true;
            }
            select.appendChild(opt);
        });
        if (!defaultFound) select.selectedIndex = 0;
    } catch (err) {
        console.error('Failed to load addresses', err);
    }
}

async function submitAddress() {
    const label     = document.getElementById('labelInput').value.trim() || 'Other';
    const address   = document.getElementById('addressInput').value.trim();
    const isDefault = document.getElementById('defaultCheck').checked;

    if (!address) { alert('Address is required'); return; }

    try {
        const res = await fetch('../AddAddressServlet', {
            method:  'POST',
            headers: {'Content-Type': 'application/json'},
            body:    JSON.stringify({ label, address, is_default: isDefault })
        });
        const d = await res.json();
        if (res.ok) {
            closeAddressPopup();
            loadAddresses();
            document.getElementById('labelInput').value     = '';
            document.getElementById('addressInput').value   = '';
            document.getElementById('defaultCheck').checked = false;
        } else {
            alert(d.error || 'Failed to save address');
        }
    } catch (err) {
        alert('Error saving address');
    }
}

// ── Balance ───────────────────────────────────────────────────────────────────

async function loadBalance() {
    try {
        const res  = await fetch('../AccountServlet');
        const d    = await res.json();
        if (d.balance !== undefined) {
            const balStr = `Balance: $${d.balance.toFixed(2)}`;
            const el1 = document.getElementById('balance');
            const el2 = document.getElementById('nav-balance');
            if (el1) el1.textContent = balStr;
            if (el2) el2.textContent = balStr;
        }
    } catch (err) {
        console.error('Balance load failed', err);
    }
}

function logout() {
    window.location.href = '../LogoutServlet';
}

// ── Fetch menu & init ─────────────────────────────────────────────────────────

async function fetchMenus() {
    try {
        const res = await fetch('../MenuServlet');
        if (!res.ok) throw new Error('Failed to load menu');
        data = await res.json();
//        console.log(data);
        renderItems(data);
        buildFilters(data);
    } catch (err) {
        menus.innerHTML = '<p style="padding:20px;color:red">Failed to load menu. Please refresh.</p>';
        console.error(err);
    }
}

fetchMenus();
loadBalance();
