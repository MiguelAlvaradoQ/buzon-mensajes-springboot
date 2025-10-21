// src/main/resources/static/js/app-admin.js

document.addEventListener('DOMContentLoaded', () => {
    const feedback = document.getElementById('feedback');
    const mensajesList = document.getElementById('mensajesList');

    let currentPage = 0;
    const pageSize = 10;
    let currentFilter = 'all';

    // Cargar estadísticas y mensajes al iniciar
    loadStats();
    loadMessages();

    // ===== CARGAR ESTADÍSTICAS =====
    async function loadStats() {
        try {
            const res = await fetch('/api/mensajes');
            if (!res.ok) return;

            const mensajes = await res.json();
            const total = mensajes.length;
            const noLeidos = mensajes.filter(m => !m.leido).length;
            const leidos = total - noLeidos;

            document.getElementById('totalMensajes').textContent = total;
            document.getElementById('noLeidos').textContent = noLeidos;
            document.getElementById('leidos').textContent = leidos;
        } catch (err) {
            console.error('Error al cargar estadísticas:', err);
        }
    }

    // ===== CARGAR MENSAJES CON PAGINACIÓN =====
    async function loadMessages(page = 0) {
        mensajesList.innerHTML = '<p class="muted">Cargando mensajes...</p>';

        try {
            let url = `/api/mensajes/paginado?page=${page}&size=${pageSize}`;

            // Aplicar filtro si está activo
            if (currentFilter === 'unread') {
                url = `/api/mensajes/paginado/filtrado?leido=false&page=${page}&size=${pageSize}`;
            } else if (currentFilter === 'read') {
                url = `/api/mensajes/paginado/filtrado?leido=true&page=${page}&size=${pageSize}`;
            }

            const res = await fetch(url);
            if (!res.ok) {
                mensajesList.innerHTML = `<p class="muted error">Error al cargar mensajes: ${res.status}</p>`;
                return;
            }

            const paginaData = await res.json();
            currentPage = paginaData.number;
            renderMensajes(paginaData.content);
            renderPaginacion(paginaData);
        } catch (err) {
            mensajesList.innerHTML = `<p class="muted error">Error de red: ${err.message}</p>`;
        }
    }

    // ===== RENDERIZAR MENSAJES =====
    function renderMensajes(mensajes) {
        if (!mensajes || mensajes.length === 0) {
            mensajesList.innerHTML = '<p class="muted">No hay mensajes con este filtro.</p>';
            return;
        }

        const html = mensajes.map(m => {
            const leidoClass = m.leido ? 'mensaje leido' : 'mensaje';
            const fecha = m.fechaCreacion ? new Date(m.fechaCreacion).toLocaleString('es-ES') : '';
            return `
        <article class="${leidoClass}" data-id="${m.id}">
          <div class="meta">
            <strong class="nombre">${escapeHtml(m.nombre)}</strong>
            <span class="email">${escapeHtml(m.email)}</span>
            <span class="fecha">${fecha}</span>
          </div>
          <p class="contenido">${escapeHtml(m.contenido)}</p>
          <div class="controls">
            ${m.leido
                ? '<span class="badge">✅ Leído</span>'
                : `<button class="small" data-action="mark" data-id="${m.id}">Marcar como leído</button>`
            }
            <button class="small danger" data-action="delete" data-id="${m.id}">🗑️ Eliminar</button>
          </div>
        </article>
      `;
        }).join('');

        mensajesList.innerHTML = html;

        // Event listeners
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

    // ===== RENDERIZAR PAGINACIÓN =====
    function renderPaginacion(paginaData) {
        const paginacionDiv = document.getElementById('paginacion');
        const { number, totalPages, first, last, totalElements } = paginaData;

        if (totalPages <= 1) {
            paginacionDiv.innerHTML = '';
            return;
        }

        let html = `
            <div class="paginacion-info">
                Página ${number + 1} de ${totalPages} (Total: ${totalElements} mensajes)
            </div>
            <div class="paginacion-controles">
        `;

        html += `<button class="small" ${first ? 'disabled' : ''} onclick="cambiarPagina(0)">❮❮ Primera</button>`;
        html += `<button class="small" ${first ? 'disabled' : ''} onclick="cambiarPagina(${number - 1})">❮ Anterior</button>`;

        for (let i = Math.max(0, number - 2); i <= Math.min(totalPages - 1, number + 2); i++) {
            const active = i === number ? 'active' : '';
            html += `<button class="small ${active}" onclick="cambiarPagina(${i})">${i + 1}</button>`;
        }

        html += `<button class="small" ${last ? 'disabled' : ''} onclick="cambiarPagina(${number + 1})">Siguiente ❯</button>`;
        html += `<button class="small" ${last ? 'disabled' : ''} onclick="cambiarPagina(${totalPages - 1})">Última ❯❯</button>`;
        html += `</div>`;

        paginacionDiv.innerHTML = html;
    }

    // ===== MARCAR COMO LEÍDO =====
    async function marcarComoLeido(id) {
        try {
            const res = await fetch(`/api/mensajes/${id}/leido`, { method: 'PATCH' });
            if (res.ok) {
                feedback.className = 'feedback success';
                feedback.textContent = '✅ Mensaje marcado como leído.';
                await loadStats();
                await loadMessages(currentPage);
                setTimeout(() => { feedback.className = 'feedback'; }, 3000);
            } else {
                feedback.className = 'feedback error';
                feedback.textContent = `❌ Error ${res.status} al marcar como leído.`;
            }
        } catch (err) {
            feedback.className = 'feedback error';
            feedback.textContent = '❌ Error de red: ' + err.message;
        }
    }

    // ===== ELIMINAR MENSAJE =====
    async function eliminarMensaje(id) {
        try {
            const res = await fetch(`/api/mensajes/${id}`, { method: 'DELETE' });
            if (res.status === 204) {
                feedback.className = 'feedback success';
                feedback.textContent = '✅ Mensaje eliminado correctamente.';
                await loadStats();
                await loadMessages(currentPage);
                setTimeout(() => { feedback.className = 'feedback'; }, 3000);
            } else {
                feedback.className = 'feedback error';
                feedback.textContent = `❌ Error ${res.status} al eliminar.`;
            }
        } catch (err) {
            feedback.className = 'feedback error';
            feedback.textContent = '❌ Error de red: ' + err.message;
        }
    }

    // ===== FILTRAR MENSAJES =====
    window.filtrarMensajes = function(filter) {
        currentFilter = filter;
        currentPage = 0;
        loadMessages(0);
    };

    // ===== CAMBIAR PÁGINA =====
    window.cambiarPagina = function(page) {
        loadMessages(page);
    };

    // ===== ESCAPE HTML =====
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