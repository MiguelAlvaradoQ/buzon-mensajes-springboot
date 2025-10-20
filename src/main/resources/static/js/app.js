// JavaScript para enviar el formulario y para listar / manipular mensajes
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('mensajeForm');
    const submitBtn = document.getElementById('submitBtn');
    const feedback = document.getElementById('feedback');
    const mensajesList = document.getElementById('mensajesList');

    // Cargar la lista de mensajes al iniciar
    loadMessages();

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        feedback.textContent = '';
        submitBtn.disabled = true;
        submitBtn.textContent = 'Enviando...';

        const payload = {
            nombre: document.getElementById('nombre').value.trim(),
            email: document.getElementById('email').value.trim(),
            contenido: document.getElementById('contenido').value.trim()
        };

        try {
            const res = await fetch('/api/mensajes', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                const data = await res.json();
                feedback.className = 'feedback success';
                feedback.textContent = 'Mensaje enviado correctamente. ID: ' + data.id;
                form.reset();
                await loadMessages(); // refrescar lista
            } else {
                let text;
                try {
                    const err = await res.json();
                    text = err.message || JSON.stringify(err);
                } catch (_) {
                    text = await res.text();
                }
                feedback.className = 'feedback error';
                feedback.textContent = `Error ${res.status}: ${text}`;
            }
        } catch (err) {
            feedback.className = 'feedback error';
            feedback.textContent = 'Error de red: ' + err.message;
        } finally {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Enviar mensaje';
        }
    });

    // Función para cargar mensajes y renderizar la lista
    async function loadMessages() {
        mensajesList.innerHTML = '<p class="muted">Cargando mensajes...</p>';
        try {
            const res = await fetch('/api/mensajes');
            if (!res.ok) {
                mensajesList.innerHTML = `<p class="muted error">Error al cargar mensajes: ${res.status}</p>`;
                return;
            }
            const mensajes = await res.json();
            renderMensajes(mensajes);
        } catch (err) {
            mensajesList.innerHTML = `<p class="muted error">Error de red: ${err.message}</p>`;
        }
    }

    // Render de la lista
    function renderMensajes(mensajes) {
        if (!mensajes || mensajes.length === 0) {
            mensajesList.innerHTML = '<p class="muted">No hay mensajes aún.</p>';
            return;
        }

        const html = mensajes.map(m => {
            const leidoClass = m.leido ? 'mensaje leido' : 'mensaje';
            const fecha = m.fechaCreacion ? new Date(m.fechaCreacion).toLocaleString() : '';
            return `
        <article class="${leidoClass}" data-id="${m.id}">
          <div class="meta">
            <strong class="nombre">${escapeHtml(m.nombre)}</strong>
            <span class="email">${escapeHtml(m.email)}</span>
            <span class="fecha">${fecha}</span>
          </div>
          <p class="contenido">${escapeHtml(m.contenido)}</p>
          <div class="controls">
            ${m.leido ? '<span class="badge">Leído</span>' : `<button class="small" data-action="mark" data-id="${m.id}">Marcar como leído</button>`}
            <button class="small danger" data-action="delete" data-id="${m.id}">Eliminar</button>
          </div>
        </article>
      `;
        }).join('');
        mensajesList.innerHTML = html;

        // Añadir listeners a los botones dinámicos
        mensajesList.querySelectorAll('button[data-action="mark"]').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const id = e.currentTarget.dataset.id;
                await marcarComoLeido(id);
            });
        });
        mensajesList.querySelectorAll('button[data-action="delete"]').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const id = e.currentTarget.dataset.id;
                if (!confirm('¿Eliminar este mensaje? Esta acción no se puede deshacer.')) return;
                await eliminarMensaje(id);
            });
        });
    }

    async function marcarComoLeido(id) {
        try {
            const res = await fetch(`/api/mensajes/${id}/leido`, { method: 'PATCH' });
            if (res.ok) {
                feedback.className = 'feedback success';
                feedback.textContent = 'Mensaje marcado como leído.';
                await loadMessages();
            } else {
                feedback.className = 'feedback error';
                feedback.textContent = `Error ${res.status} al marcar como leído.`;
            }
        } catch (err) {
            feedback.className = 'feedback error';
            feedback.textContent = 'Error de red: ' + err.message;
        }
    }

    async function eliminarMensaje(id) {
        try {
            const res = await fetch(`/api/mensajes/${id}`, { method: 'DELETE' });
            if (res.status === 204) {
                feedback.className = 'feedback success';
                feedback.textContent = 'Mensaje eliminado correctamente.';
                await loadMessages();
            } else {
                feedback.className = 'feedback error';
                feedback.textContent = `Error ${res.status} al eliminar.`;
            }
        } catch (err) {
            feedback.className = 'feedback error';
            feedback.textContent = 'Error de red: ' + err.message;
        }
    }

    // Util: escapar HTML para evitar XSS
    function escapeHtml(str) {
        if (!str) return '';
        return str
            .replaceAll('&', '&amp;')
            .replaceAll('<', '&lt;')
            .replaceAll('>', '&gt;')
            .replaceAll('"', '&quot;')
            .replaceAll("'", '&#039;');
    }
});