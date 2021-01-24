package tunts.test;

/**
 * Classe usada para representar as entradas de alunos.
 * 
 * @author Vítor Yago Argus
 * @data 19/01/2021
 */
public class Aluno {
	private int matricula;
	private String nome;
	private int faltas;
	private float p1;
	private float p2;
	private float p3;
	private String situacao;
	private float notaParaAprovacao;

	/**
	 * 
	 * @param matricula Matrícula do aluno.
	 * @param nome		Nome do aluno.
	 * @param faltas	Número de faltas do aluno.
	 * @param p1		Nota da primeira prova do aluno.
	 * @param p2		Nota da segunda prova do aluno.
	 * @param p3		Nota da terceira prova do aluno.
	 */
	public Aluno(int matricula, String nome, int faltas, float p1, float p2, float p3) {
		super();
		this.matricula = matricula;
		this.nome = nome;
		this.faltas = faltas;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		atualizar(faltas, p1, p2, p3);
	}

	/**
	 * Método que verifica a situação do aluno.
	 * Também calcula sua nota para aprovação, caso aplicável.
	 */
	private void atualizar(int faltas, float p1, float p2, float p3) {
		float media = (p1 + p2 + p3) / 3;
		if (faltas > 15) {
			situacao = "Reprovado por Faltas";
			notaParaAprovacao = 0;
		} else if (media < 50) {
			situacao = "Reprovado por Nota";
			notaParaAprovacao = 0;
		} else if (media < 70) {
			situacao = "Exame Final";
			notaParaAprovacao = 100 - media;
		} else {
			situacao = "Aprovado";
			notaParaAprovacao = 0;
		}
	}

	// Getters e Setters
	public int getMatricula() {
		return matricula;
	}

	public void setMatricula(int matricula) {
		this.matricula = matricula;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getFalta() {
		return faltas;
	}

	public void setFalta(int faltas) {
		this.faltas = faltas;
	}

	public float getP1() {
		return p1;
	}

	public void setP1(float p1) {
		this.p1 = p1;
	}

	public float getP2() {
		return p2;
	}

	public void setP2(float p2) {
		this.p2 = p2;
	}

	public float getP3() {
		return p3;
	}

	public void setP3(float p3) {
		this.p3 = p3;
	}

	public String getSituacao() {
		return situacao;
	}

	public float getNotaParaAprovacao() {
		return notaParaAprovacao;
	}
}
