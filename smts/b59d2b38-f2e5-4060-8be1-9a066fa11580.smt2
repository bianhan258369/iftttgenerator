(assert (forall ((Person.distanceFromPro Int)) (exists ((Projector Int)) (and (=> (<= Person.distanceFromPro 2) (= Projector 0))(=> (> Person.distanceFromPro 2) (= Projector 1))))))
(assert (forall ((Air.humidity Int)) (exists ((Window Int)) (and (=> (< Air.humidity 30) (= Window 0))(=> (>= Air.humidity 30) (= Window 1))))))
(assert (forall ((Air.humidity Int)) (exists ((Blind Int)) (and (=> (< Air.humidity 30) (= Blind 0))(=> (>= Air.humidity 30) (= Blind 1))))))
(assert (forall ((Air.temperature Int)) (exists ((Ac Int)) (and (=> (>= Air.temperature 20.0) (= Ac 0))(=> (< Air.temperature 20.0) (= Ac 1))))))
(assert (forall ((CO.ppm Int)) (exists ((Af Int)) (and (=> (> CO.ppm 5.0) (= Af 0))(=> (<= CO.ppm 5.0) (= Af 1))))))
(assert (forall ((Light.brightness Int)) (exists ((Bulb Int)) (and (=> (< Light.brightness 35) (= Bulb 0))(=> (>= Light.brightness 35) (= Bulb 1))))))
(assert (forall ((Person.distanceFromMc Int)) (exists ((Mc Int)) (and (=> (<= Person.distanceFromMc 2) (= Mc 0))(=> (> Person.distanceFromMc 2) (= Mc 1))))))
(assert (forall ((Air.humidity Int)) (exists ((Ah Int)) (and (=> (< Air.humidity 5.5) (= Ah 0))(=> (>= Air.humidity 5.5) (= Ah 1))))))
(check-sat)