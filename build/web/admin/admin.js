let allOrders      = [];
let currentOrderId = null;
let currentRole    = null;

document.addEventListener('DOMContentLoaded', () => {
    loadOrders();
});

function showSection(name) {
    document.querySelectorAll('.admin-section').forEach(s => s.style.display = 'none');
    document.getElementById(name).style.display = 'block';
    if (name === 'orders') loadOrders();
    if (name === 'menu')   loadMenu();
    if (name === 'staff')  loadUsers();
}

// ── ORDERS ────────────────────────────────────────────────────────────────────

async function loadOrders() {
    try {
        const res = await fetch('../AdminServlet?action=getOrders&status=all');
        allOrders = await res.json();
        renderOrders(allOrders);
    } catch (err) {
        console.error('Failed to load orders', err);
    }
}

function filterOrders() {
    const status   = document.getElementById('statusFilter').value;
    const filtered = status === 'all' ? allOrders : allOrders.filter(o => o.status === status);
    renderOrders(filtered);
}

function renderOrders(orders) {
    const body = document.getElementById('orderTableBody');
    body.innerHTML = '';
    orders.forEach(order => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${order.id}</td>
            <td>Customer #${order.customerId}</td>
            <td>$${order.totalAmount.toFixed(2)}</td>
            <td>${order.deliveryAddress}</td>
            <td>${order.status}</td>
        `;
        const actions = document.createElement('td');
        if (order.status === 'paid') {
            const btn = document.createElement('button');
            btn.textContent = 'Assign Chef';
            btn.onclick = () => openAssignModal(order.id, 'chef');
            actions.appendChild(btn);
        }
        if (order.status === 'ready') {
            const btn = document.createElement('button');
            btn.textContent = 'Assign Delivery';
            btn.onclick = () => openAssignModal(order.id, 'delivery');
            actions.appendChild(btn);
        }
        if (['paid','preparing','ready'].includes(order.status)) {
            const btn = document.createElement('button');
            btn.textContent = 'Refund';
            btn.onclick = () => refundOrder(order.id);
            actions.appendChild(btn);
        }
        row.appendChild(actions);
        body.appendChild(row);
    });
}

// ── ASSIGN MODAL ──────────────────────────────────────────────────────────────

async function openAssignModal(orderId, role) {
    currentOrderId = orderId;
    currentRole    = role;
    document.getElementById('targetOrderId').textContent  = orderId;
    document.getElementById('staffTypeLabel').textContent = role === 'chef' ? 'Chef' : 'Delivery Guy';
    document.getElementById('assignModal').style.display  = 'flex';
    const res    = await fetch(`../AdminServlet?action=getStaff&role=${role}`);
    const staff  = await res.json();
    const select = document.getElementById('staffSelect');
    select.innerHTML = '<option value="">-- Select --</option>';
    staff.forEach(person => {
        const opt = document.createElement('option');
        opt.value = person.id; opt.textContent = person.name;
        select.appendChild(opt);
    });
}

async function submitAssignment() {
    const staffId = parseInt(document.getElementById('staffSelect').value);
    if (!staffId) { alert('Please select a staff member'); return; }
    const action = currentRole === 'chef' ? 'assignChef' : 'assignDelivery';
    const res    = await fetch('../AdminServlet', {
        method: 'POST', headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({ action, orderId: currentOrderId, staffId })
    });
    const data = await res.json();
    if (data.message) { alert(data.message); closeModal(); loadOrders(); }
    else alert('Failed: ' + data.error);
}

function closeModal() {
    document.getElementById('assignModal').style.display = 'none';
}

// ── REFUND ────────────────────────────────────────────────────────────────────

async function refundOrder(orderId) {
    if (!confirm(`Refund order #${orderId}? This will cancel the order and return money to the customer.`)) return;
    const res  = await fetch('../AdminServlet', {
        method: 'POST', headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({ action: 'refund', orderId })
    });
    const data = await res.json();
    if (data.message) { alert(data.message); loadOrders(); }
    else alert('Refund failed: ' + data.error);
}

// ── MENU ──────────────────────────────────────────────────────────────────────

async function loadMenu() {
    const res   = await fetch('../AdminServlet?action=getMenu');
    const items = await res.json();
    renderMenu(items);
}

function renderMenu(items) {
    const body = document.getElementById('menuTableBody');
    body.innerHTML = '';
    items.forEach(item => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${item.name}</td>
            <td>${item.categoryName}</td>
            <td>$${item.price.toFixed(2)}</td>
            <td>${item.isAvailable === 1 ? 'Yes' : 'No'}</td>
        `;
        const actionTd = document.createElement('td');
        const toggleBtn = document.createElement('button');
        toggleBtn.textContent = item.isAvailable === 1 ? 'Make Unavailable' : 'Make Available';
        toggleBtn.onclick = () => toggleMenuItem(item.id);
        actionTd.appendChild(toggleBtn);
        const priceBtn = document.createElement('button');
        priceBtn.textContent = 'Change Price';
        priceBtn.onclick = () => updatePrice(item.id, item.price);
        actionTd.appendChild(priceBtn);
        row.appendChild(actionTd);
        body.appendChild(row);
    });
}

async function toggleMenuItem(itemId) {
    const res  = await fetch('../AdminServlet', {
        method: 'POST', headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({ action: 'toggleMenu', itemId })
    });
    const data = await res.json();
    if (data.message) loadMenu();
}

async function addMenuItem() {
    const name        = document.getElementById('itemName').value;
    const description = document.getElementById('itemDescription').value;
    const price       = parseFloat(document.getElementById('itemPrice').value);
    const categoryId  = parseInt(document.getElementById('itemCategory').value);
    if (!name || !price || !categoryId) { alert('Please fill in all fields'); return; }
    const res  = await fetch('../AdminServlet', {
        method: 'POST', headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({ action: 'addMenuItem', name, description, price, categoryId })
    });
    const data = await res.json();
    if (data.message) {
        alert('Item added');
        document.getElementById('itemName').value        = '';
        document.getElementById('itemDescription').value = '';
        document.getElementById('itemPrice').value       = '';
        document.getElementById('itemCategory').value    = '';
        loadMenu();
    }
}

async function updatePrice(itemId, currentPrice) {
    const newPrice = prompt(`Enter new price (current: $${currentPrice}):`);
    if (newPrice === null) return;
    const parsed = parseFloat(newPrice);
    if (isNaN(parsed) || parsed <= 0) { alert('Please enter a valid price'); return; }
    const res  = await fetch('../AdminServlet', {
        method: 'POST', headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({ action: 'updatePrice', itemId, price: parsed })
    });
    const data = await res.json();
    if (data.message) { alert('Price updated'); loadMenu(); }
    else alert('Failed: ' + data.error);
}

// ── STAFF ─────────────────────────────────────────────────────────────────────

async function loadUsers() {
    try {
        const res   = await fetch('../AdminServlet?action=getAllUsers');
        const users = await res.json();
        const body  = document.getElementById('usersTableBody');
        body.innerHTML = '';
        users.forEach(u => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${u.id}</td>
                <td>${u.name}</td>
                <td>${u.email}</td>
                <td>${u.phone || '—'}</td>
                <td><span style="background:${roleColor(u.role)};color:white;padding:3px 10px;border-radius:20px;font-size:0.8rem">${u.role}</span></td>
            `;
            const actionTd = document.createElement('td');
            const delBtn   = document.createElement('button');
            delBtn.textContent = 'Delete';
            delBtn.style.background = '#e74c3c';
            delBtn.style.color      = 'white';
            delBtn.onclick = () => deleteUser(u.id, u.name);
            actionTd.appendChild(delBtn);
            row.appendChild(actionTd);
            body.appendChild(row);
        });
    } catch (err) {
        console.error('Failed to load users', err);
    }
}

function roleColor(role) {
    switch (role) {
        case 'admin':    return '#8e44ad';
        case 'chef':     return '#e67e22';
        case 'delivery': return '#27ae60';
        default:         return '#2980b9';
    }
}

async function registerStaff() {
    const name     = document.getElementById('staffName').value.trim();
    const email    = document.getElementById('staffEmail').value.trim();
    const phone    = document.getElementById('staffPhone').value.trim();
    const password = document.getElementById('staffPassword').value;
    const role     = document.getElementById('staffRole').value;
    const msgEl    = document.getElementById('staffMsg');

    if (!email || !password || !role) {
        msgEl.innerHTML = '<p style="color:red">Email, password and role are required.</p>';
        return;
    }
    if (password.length < 6) {
        msgEl.innerHTML = '<p style="color:red">Password must be at least 6 characters.</p>';
        return;
    }

    try {
        const res  = await fetch('../AdminServlet', {
            method: 'POST', headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ action: 'registerStaff', name, email, phone, password, role })
        });
        const data = await res.json();
        if (res.ok) {
            msgEl.innerHTML = `<p style="color:green">✅ ${data.message}</p>`;
            document.getElementById('staffName').value     = '';
            document.getElementById('staffEmail').value    = '';
            document.getElementById('staffPhone').value    = '';
            document.getElementById('staffPassword').value = '';
            document.getElementById('staffRole').value     = '';
            loadUsers();
        } else {
            msgEl.innerHTML = `<p style="color:red">❌ ${data.error}</p>`;
        }
    } catch (err) {
        msgEl.innerHTML = '<p style="color:red">❌ Failed to create account.</p>';
    }
}

async function deleteUser(userId, userName) {
    if (!confirm(`Delete account for "${userName}"? This cannot be undone.`)) return;
    try {
        const res  = await fetch('../AdminServlet', {
            method: 'POST', headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ action: 'deleteUser', userId })
        });
        const data = await res.json();
        if (res.ok) { loadUsers(); }
        else alert('Failed: ' + data.error);
    } catch (err) {
        alert('Failed to delete user');
    }
}

function logout() {
    window.location.href = '../LogoutServlet';
}
