// myorder.js — fixed version

async function loadOrders() {
    try {
        const res = await fetch('../CustomerOrdersServlet');
        if (res.status === 401) {
            window.location.href = '../index.html';
            return;
        }
        const orders    = await res.json();
        const container = document.getElementById('orders-list');

        if (orders.length === 0) {
            container.innerHTML = `
                <div style="text-align:center;padding:40px;color:#888">
                    <p style="font-size:2rem">🍽️</p>
                    <p>No active orders.</p>
                    <a href="menu.html" style="color:#ff4757;font-weight:bold">Browse Menu →</a>
                </div>`;
            return;
        }

        container.innerHTML = '';
        orders.forEach(order => {
            const canCancel = (order.status === 'pending' || order.status === 'paid');
            const card      = document.createElement('div');
            card.className  = 'order-card';

            card.innerHTML = `
                <h3>Order #${order.id}</h3>
                <p>Status: <span class="status-tag ${order.status}">${order.status.toUpperCase()}</span></p>
                <p>Total: <strong>$${order.totalAmount.toFixed(2)}</strong></p>
                <p>Address: ${order.deliveryAddress}</p>
                ${canCancel
                    ? `<button class="btn-cancel" onclick="cancelOrder(${order.id})">Cancel &amp; Get Refund</button>`
                    : `<p class="msg-locked">🔒 Chef is preparing — cannot cancel</p>`
                }
            `;
            container.appendChild(card);
        });
    } catch (err) {
        console.error('Failed to load orders', err);
        document.getElementById('orders-list').innerHTML =
            '<p style="color:red;padding:20px">Failed to load orders. Please refresh.</p>';
    }
}

async function cancelOrder(id) {
    if (!confirm('Cancel this order? You will receive a refund if you paid by wallet/card.')) return;

    try {
        const res  = await fetch(`../CustomerOrdersServlet?orderId=${id}`, { method: 'POST' });
        const data = await res.json();

        if (res.ok) {
            alert(data.message);
            loadOrders();
            loadBalance();
        } else {
            alert('Cannot cancel: ' + (data.error || 'Unknown error'));
        }
    } catch (err) {
        alert('Network error. Please try again.');
    }
}

async function loadBalance() {
    try {
        const res  = await fetch('../AccountServlet');
        const data = await res.json();
        if (data.balance !== undefined) {
            document.getElementById('nav-balance').textContent = `Balance: $${data.balance.toFixed(2)}`;
        }
    } catch (err) {}
}

function logout() {
    window.location.href = '../LogoutServlet';
}

// Auto-refresh every 30 seconds so status updates automatically
window.onload = () => {
    loadOrders();
    loadBalance();
    setInterval(loadOrders, 30000);
};
