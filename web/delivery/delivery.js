// delivery.js — fixed version

async function loadDeliveries() {
    try {
        const response = await fetch('../DeliveryServlet');
        if (response.status === 401) {
            window.location.href = '../index.html';
            return;
        }
        const orders    = await response.json();
        const container = document.getElementById('deliveryList');

        if (orders.length === 0) {
            container.innerHTML = `
                <div class="empty-state" style="text-align:center;padding:40px;color:#888">
                    <p style="font-size:3rem">🚚</p>
                    <h3>No orders ready for delivery!</h3>
                    <p>Waiting for chefs to finish cooking...</p>
                </div>`;
            return;
        }

        container.innerHTML = orders.map(order => `
            <div class="order-card" style="background:white;margin-bottom:20px;border-radius:15px;
                overflow:hidden;box-shadow:0 4px 12px rgba(0,0,0,0.1)">
                <div style="background:#27ae60;color:white;padding:15px;font-weight:bold;
                    display:flex;justify-content:space-between">
                    <span>ORDER #${order.id}</span>
                    <span>$${order.total.toFixed(2)}</span>
                </div>
                <div style="padding:20px">
                    <p style="background:#e8f5e9;padding:12px;border-radius:8px;
                        font-size:1.1rem;color:#1e8449;margin-bottom:15px">
                        📍 <strong>${order.address}</strong>
                    </p>
                    <button onclick="completeDelivery(${order.id})"
                        style="display:block;width:100%;padding:15px;background:#27ae60;
                        color:white;border:none;font-size:1.1rem;font-weight:bold;
                        border-radius:8px;cursor:pointer">
                        ✅ Confirm Delivered
                    </button>
                </div>
            </div>
        `).join('');
    } catch (err) {
        console.error('Failed to load deliveries', err);
    }
}

async function completeDelivery(orderId) {
    if (!confirm('Confirm that this order has been delivered to the customer?')) return;

    const response = await fetch(`../DeliveryServlet?orderId=${orderId}`, {
        method: 'POST'
    });
    const data = await response.json();

    if (response.ok) {
        alert(data.message || 'Delivery confirmed! You are now available for new orders.');
        loadDeliveries();
    } else {
        alert('Error: ' + (data.error || 'Failed to confirm delivery'));
    }
}

function logout() {
    window.location.href = '../LogoutServlet';
}

loadDeliveries();
setInterval(loadDeliveries, 30000);
