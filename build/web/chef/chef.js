// chef.js — fixed version

async function loadChefTasks() {
    try {
        const response = await fetch('../ChefServlet');
        if (response.status === 401) {
            window.location.href = '../index.html';
            return;
        }
        const orders    = await response.json();
        const container = document.getElementById('chefOrderList');

        if (orders.length === 0) {
            container.innerHTML = `
                <div class="empty-state" style="text-align:center;padding:40px;color:#888">
                    <p style="font-size:3rem">🍳</p>
                    <h3>No active cooking tasks!</h3>
                    <p>Waiting for new orders to be assigned...</p>
                </div>`;
            return;
        }

        container.innerHTML = orders.map(order => `
            <div class="order-card">
                <span class="order-id" style="background:#2980b9;color:white;padding:4px 12px;border-radius:20px;font-weight:bold">
                    ORDER #${order.id}
                </span>
                <div class="order-info" style="margin:15px 0">
                    <p><strong>🍽️ Items:</strong> ${order.items || 'N/A'}</p>
                    <p><strong>📍 Deliver to:</strong> ${order.address}</p>
                    <p><strong>📝 Notes:</strong> ${order.notes || 'No special instructions'}</p>
                </div>
                <button class="btn-action btn-chef" onclick="markReady(${order.id})"
                    style="width:100%;padding:12px;background:#2980b9;color:white;border:none;border-radius:8px;font-weight:bold;cursor:pointer;font-size:1rem">
                    ✅ Mark as Ready for Delivery
                </button>
            </div>
        `).join('');
    } catch (err) {
        console.error('Failed to load chef tasks', err);
    }
}

async function markReady(orderId) {
    if (!confirm('Is this order ready for the delivery driver?')) return;

    const response = await fetch(`../ChefServlet?orderId=${orderId}`, {
        method: 'POST'
    });
    const data = await response.json();

    if (response.ok) {
        alert(data.message || 'Order marked as ready!');
        loadChefTasks();
    } else {
        alert('Error: ' + (data.error || 'Failed to update order'));
    }
}

function logout() {
    window.location.href = '../LogoutServlet';
}

loadChefTasks();
setInterval(loadChefTasks, 30000);
