// account.js — fixed version

async function loadProfile() {
    try {
        const res  = await fetch('../AccountServlet');
        if (res.status === 401) {
            window.location.href = '../index.html';
            return;
        }
        const data = await res.json();
        if (data.balance !== undefined) {
            document.getElementById('balance-display').textContent = `$${data.balance.toFixed(2)}`;
            document.getElementById('nav-balance').textContent     = `Balance: $${data.balance.toFixed(2)}`;
        }
        if (data.name)  document.getElementById('acc-name').value  = data.name;
        if (data.phone) document.getElementById('acc-phone').value = data.phone;
    } catch (err) {
        console.error('Failed to load profile', err);
    }
}

async function updateAccount() {
    const name  = document.getElementById('acc-name').value.trim();
    const phone = document.getElementById('acc-phone').value.trim();

    if (!name) { alert('Name cannot be empty'); return; }

    try {
        const res  = await fetch(`../AccountServlet?action=update&name=${encodeURIComponent(name)}&phone=${encodeURIComponent(phone)}`,
            { method: 'POST' });
        const data = await res.json();
        alert(data.message || 'Profile updated!');
    } catch (err) {
        alert('Failed to update profile');
    }
}

async function deleteAccount() {
    if (!confirm('Are you sure? This will permanently delete your account and cannot be undone.')) return;

    try {
        await fetch('../AccountServlet?action=delete', { method: 'POST' });
        window.location.href = '../index.html';
    } catch (err) {
        alert('Failed to delete account');
    }
}

function logout() {
    window.location.href = '../LogoutServlet';
}

loadProfile();
