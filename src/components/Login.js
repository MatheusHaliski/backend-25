import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import Swal from "sweetalert2";
import "./Login.css";
import Header from "./Header";
import Footer from "./Footer";
import EsqueciSenha from "./EsqueciSenha";

function Login() {
    const [email, setEmail] = useState("");
    const [senha, setSenha] = useState("");
    const [mostrarSenha, setMostrarSenha] = useState(false);
    const navigate = useNavigate();

    const validarEmail = (email) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    const validarSenha = (senha) => /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/.test(senha);

    const exibirErro = (mensagem) => {
        Swal.fire({ icon: "error", title: "Erro", text: mensagem });
    };

    const exibirSucesso = (mensagem) => {
        Swal.fire({ icon: "success", title: "Sucesso", text: mensagem });
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!validarEmail(email)) {
            exibirErro("Formato de e-mail inválido.");
            return;
        }

        const formData = new URLSearchParams();
        formData.append("email", email);
        formData.append("password", senha);

        try {
            const response = await fetch("http://backend-25v2.onrender.com/pessoas/pessoas12", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: formData.toString(),
                credentials: "include"
            });

            if (response.ok) {
                const data = await response.json();
                sessionStorage.setItem("emailUsuario", email);
                sessionStorage.setItem("tipoUsuario", data.tipoUsuario);
                exibirSucesso("Login realizado com sucesso!");
                setTimeout(() => {
                    navigate(data.tipoUsuario === "USUARIO_ADM" ? "/MenuInicial2" : "/MenuInicial");
                }, 1500);
            } else if (response.status === 401) {
                const erro = await response.json();
                exibirErro(erro.erro || "Email ou senha inválidos.");
            } else {
                const erro = await response.text();
                exibirErro("Erro no login: " + erro);
            }
        } catch (error) {
            console.error("Erro na requisição:", error);
            exibirErro("Erro na requisição.");
        }
    };

    return (
        <div className="d-flex flex-column min-vh-100">
            <Header />
            <div className="flex-grow-1 d-flex align-items-center justify-content-center bg-light">
                <div className="row w-100 shadow-lg rounded" style={{ maxWidth: "1200px", backgroundColor: "white", minHeight: "80vh" }}>
                    {/* Lado esquerdo com imagem */}
                    <div className="col-md-6 d-none d-md-block p-0">
                        <div
                            style={{
                                height: "100%",
                                backgroundImage: "url('assets/btn-register04.jpeg')",
                                backgroundSize: "cover",
                                backgroundPosition: "center",
                                borderTopLeftRadius: "0.5rem",
                                borderBottomLeftRadius: "0.5rem"
                            }}
                        ></div>
                    </div>

                    {/* Lado direito com formulário */}
                    <div className="col-md-6 p-5 d-flex flex-column justify-content-center">
                        <h2 className="mb-4 text-center text-black">Login</h2>
                        <form onSubmit={handleSubmit}>
                            <div className="mb-3">
                                <input
                                    type="email"
                                    className="form-control"
                                    placeholder="Digite seu e-mail"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                />
                            </div>
                            <div className="mb-3 position-relative">
                                <input
                                    type={mostrarSenha ? "text" : "password"}
                                    className="form-control"
                                    placeholder="Digite sua senha"
                                    value={senha}
                                    onChange={(e) => setSenha(e.target.value)}
                                    required
                                />
                                <span
                                    onClick={() => setMostrarSenha(!mostrarSenha)}
                                    style={{
                                        position: "absolute",
                                        top: "50%",
                                        right: "15px",
                                        transform: "translateY(-50%)",
                                        cursor: "pointer",
                                        color: "#6c757d"
                                    }}
                                >
                                    {mostrarSenha ? <FaEyeSlash /> : <FaEye />}
                                </span>
                            </div>
                            <div className="d-grid">
                                <button type="submit" className="btn btn-primary">Entrar</button>
                            </div>
                        </form>

                        <div className="mt-3 text-center">
                        <span
                            onClick={handleEsqueciSenha}
                            className="text-decoration-none text-primary"
                            style={{ cursor: "pointer" }}
                            >
                        Esqueci minha senha
                        </span>
                            <br/>
                            <Link to="/registrarusuario" className="text-decoration-none text-success">
                                Criar nova conta
                            </Link>
                        </div>
                    </div>
                </div>
            </div>
            <Footer />
        </div>
    );
}

const handleEsqueciSenha = async (navigate) => {
    const { value: email } = await Swal.fire({
        title: "Recuperar Senha",
        html: `<input type="email" id="email" class="swal2-input" placeholder="Digite seu e-mail" />`,
        showCancelButton: true,
        confirmButtonText: "Enviar",
        preConfirm: () => {
            const email = Swal.getPopup().querySelector('#email').value;
            if (!email) {
                Swal.showValidationMessage(`Por favor insira um e-mail`);
            }
            return email;
        }
    });

    if (email) {
        try {
            const response = await fetch("http://localhost:8080/pessoas/redefinir-senha", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                body: new URLSearchParams({ email }).toString(),
            });

            if (response.ok) {
                await Swal.fire({
                    icon: 'success',
                    title: 'E-mail enviado!',
                    text: 'Verifique seu e-mail para redefinir sua senha.',
                    confirmButtonText: 'OK'
                });
                navigate("/MenuInicial");  // redireciona se quiser
            } else {
                const erro = await response.text();
                await Swal.fire({
                    icon: 'error',
                    title: 'Erro ao enviar e-mail',
                    text: erro || 'Algo deu errado ao tentar enviar o e-mail.'
                });
            }
        } catch (error) {
            console.error("Erro ao enviar e-mail:", error);
            await Swal.fire({
                icon: 'error',
                title: 'Erro na requisição',
                text: 'Tente novamente mais tarde.'
            });
        }
    }
};


export default Login;
