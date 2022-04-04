package pl.training.jpa;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// @IdClass(PaymentId.class)
@Table(name = "payments", indexes = @Index(name = "paymentStatus", columnList = "status"))
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id") // @Id, @Embeddable
// @EqualsAndHashCode(of = {"id", "externalTransactionId"}) // @IdClass
public class Payment {

    /* @Embeddable
    @EmbeddedId
    private PaymentId id;
    */

    /* @IdClass
    @Column(length = 36)
    @Id
    private String id;
    @Column(name = "transaction_id", unique = true, length = 36)
    @Id
    private String externalTransactionId;*/

    @Column(length = 36)
    @Id
    private String id;
    @Column(name = "transaction_id", unique = true, length = 36)
    private String externalTransactionId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    /*@Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;*/
    private LocalDateTime timestamp;
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    @Lob
    private String description;
    @Transient
    private String checksum;
    // @Convert(converter = FastMoneyConverter.class)
    // private FastMoney value;
    @AttributeOverride(name = "value", column = @Column(nullable = false, precision = 10, scale = 2))
    @Embedded
    private Money money;
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> events;
    // @CollectionTable(name = "payment_properties", joinColumns = @JoinColumn(name = "payment_id", referencedColumnName = "id"))
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @ElementCollection(fetch = FetchType.LAZY)
    private Map<String, String> properties;
    @Version
    private Long version;

}
