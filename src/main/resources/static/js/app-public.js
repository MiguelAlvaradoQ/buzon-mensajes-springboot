// src/main/resources/static/js/app-public.js

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('mensajeForm');
    const submitBtn = document.getElementById('submitBtn');
    const feedback = document.getElementById('feedback');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        feedback.textContent = '';
        feedback.className = 'feedback';
        submitBtn.disabled = true;
        submitBtn.textContent = '📤 Enviando...';

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
                feedback.textContent = '✅ ¡Mensaje enviado correctamente! Te responderé pronto.';
                form.reset();

                // Ocultar el mensaje de éxito después de 5 segundos
                setTimeout(() => {
                    feedback.className = 'feedback';
                    feedback.textContent = '';
                }, 5000);
            } else {
                let text;
                try {
                    const err = await res.json();
                    text = err.message || JSON.stringify(err);
                } catch (_) {
                    text = await res.text();
                }
                feedback.className = 'feedback error';
                feedback.textContent = `❌ Error: ${text}`;
            }
        } catch (err) {
            feedback.className = 'feedback error';
            feedback.textContent = '❌ Error de conexión. Por favor, intenta de nuevo.';
        } finally {
            submitBtn.disabled = false;
            submitBtn.textContent = '📤 Enviar mensaje';
        }
    });
});